package com.hcoders.portal.controller;

// Importing standard Java classes for collections, dates, and streams
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Importing Spring Framework classes for dependency injection, security, and web MVC
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// Importing application-specific models, security details, services, and utility classes
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ConfirmationTokenService;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;
import com.hcoders.portal.utils.DateTimeUtils;

@Controller // Marks the class as a Spring MVC Controller that handles HTTP requests
public class RegistrationController {

    // Injecting UserService for operations related to users (e.g., saving, finding, assigning)
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    // Injecting TestService for operations related to tests
    @Autowired
    @Qualifier("testServiceImpl")
    private TestService testService;

    // Injecting ConfirmationTokenService for managing email confirmation tokens during registration
    @Autowired
    @Qualifier("confirmationTokenServiceImpl")
    private ConfirmationTokenService confirmationTokenService;

    // Injecting ResultService for handling test result related operations
    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService;

    // Injecting BCryptPasswordEncoder to securely hash user passwords
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Handles GET requests to "/login".
     * Displays the sign-in/sign-up page, or redirects to home if the user is already authenticated.
     *
     * @param model the model to pass attributes to the view (for data binding)
     * @param error optional parameter for login error messages
     * @param logout optional parameter indicating if the user logged out
     * @param emailConfirmation optional parameter for email confirmation messages
     * @return the view name for the sign-in/sign-up page, or a redirect to home if already authenticated
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout, String emailConfirmation) {
        // Retrieve the current authentication object from the security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is already logged in (i.e., not an anonymous user)
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            // Redirect to home if user is authenticated
            return "redirect:/";
        }
        // Add a new User object to the model for form binding
        model.addAttribute("user", new User());
        // Return the combined sign-in and sign-up view
        return "sign-in-sign-up";
    }

    /**
     * Handles GET requests to "/signup".
     * Displays the sign-up portion of the sign-in/sign-up page.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the sign-in/sign-up page
     */
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String registration(Model model) {
        // Add an empty User object to the model for the registration form
        model.addAttribute("user", new User());
        return "sign-in-sign-up";
    }

    /**
     * Handles POST requests to "/signup" for user registration.
     * It validates the provided user details, checks for duplicates,
     * and creates a new user with the appropriate role.
     *
     * @param model the model to pass attributes for error or confirmation messages
     * @param user the User object populated from the form submission
     * @param role a string indicating the role (e.g., "admin" or "user")
     * @param adminEmail the email of the admin to associate with the new user
     * @return the view name to display, or redirects based on the registration flow
     */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registration(Model model, User user, @Param("role") String role,
                               @Param("adminEmail") String adminEmail) {
        // Check if a user with the provided email already exists (ignoring case)
        User existingUser = userService.findByEmailIgnoreCase(user.getEmail());
        if (existingUser != null) {
            // If email exists, add an error message and return to the sign-in/sign-up view
            model.addAttribute("message", "This email " + user.getEmail() + " already exists!");
            return "sign-in-sign-up";
        }
        // Check if a user with the provided username already exists (ignoring case)
        existingUser = userService.findByUsernameIgnoreCase(user.getUsername());
        if (existingUser != null) {
            // If username exists, add an error message and return to the view
            model.addAttribute("message", "This User " + user.getUsername() + " already exists!");
            return "sign-in-sign-up";
        }

        // If the role parameter is provided and indicates an admin registration
        if (role != null && !role.isEmpty() && role.equalsIgnoreCase("admin")) {
            // Find an existing admin using the provided adminEmail for confirmation purposes
            User confirmationAdmin = userService.findByEmailIgnoreCase(adminEmail);
            if (confirmationAdmin == null) {
                // If no such admin exists, display an error message
                model.addAttribute("message", "No Admin found with the provided email " + adminEmail);
                return "sign-in-sign-up";
            }
            // Proceed with creating an admin user
            return createAdminUser(model, user, role, confirmationAdmin);
        } else if (role != null && !role.isEmpty() && role.equalsIgnoreCase("user")) {
            // For normal user registrations, verify that the provided adminEmail exists
            User admin = userService.findByEmailIgnoreCase(adminEmail);
            if (admin == null) {
                // If no admin found, display an error message
                model.addAttribute("message", "An Admin with the given email " + adminEmail + " doesn't exist!");
                return "sign-in-sign-up";
            }
            // Proceed with creating a normal user
            return createNormalUser(model, user, role, admin);
        }

        // If role is not valid or provided, redirect to the login page
        return "redirect:/login";
    }

    /**
     * Helper method to create a normal user.
     * It saves the user and assigns them to the specified admin.
     *
     * @param model the model to pass attributes for feedback messages
     * @param user the User object with registration details
     * @param role the role for the user, expected to be "user"
     * @param admin the admin to whom the new user will be assigned
     * @return the view name for the sign-in/sign-up page after registration
     */
    private String createNormalUser(Model model, User user, String role, User admin) {
        // Set up the user with proper role and encode the password
        user = createUserWithRole(user, role);
        // Save the user to the database
        user = userService.save(user);
        // Assign the newly created user to the given admin by their IDs
        userService.assignUser(admin.getId(), user.getId());
        // Add the email for confirmation feedback in the view
        model.addAttribute("emailConfirmation", user.getEmail());
        return "sign-in-sign-up";
    }

    /**
     * Helper method to create an admin user.
     * It saves the admin account and assigns it to an existing admin for confirmation.
     *
     * @param model the model to pass attributes for feedback messages
     * @param user the User object containing registration details for an admin
     * @param role the role for the user, expected to be "admin"
     * @param confirmationAdmin the admin who will confirm the new admin's account
     * @return the view name for the sign-in/sign-up page after registration
     */
    private String createAdminUser(Model model, User user, String role, User confirmationAdmin) {
        // Set up the user with proper role and encode the password
        user = createUserWithRole(user, role);
        // Save the new admin user to the database
        user = userService.save(user);
        // Assign this new admin to an existing admin for confirmation
        userService.assignUser(confirmationAdmin.getId(), user.getId());
        // Add a confirmation message to the model indicating pending approval
        model.addAttribute("message", "Admin account created. Awaiting confirmation from " + confirmationAdmin.getEmail());
        return "sign-in-sign-up";
    }

    /**
     * Common helper method to prepare a user object for saving.
     * It trims inputs, encodes the password, and assigns the proper role.
     *
     * @param user the User object from the registration form
     * @param role a string indicating the type of user ("admin" or "user")
     * @return the processed User object ready for saving
     */
    private User createUserWithRole(User user, String role) {
        // If the password field is non-empty, trim and encode it using BCrypt
        if (!user.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }
        // Trim the username and convert it to lowercase for consistency
        if (!user.getUsername().trim().isEmpty()) {
            user.setUsername(user.getUsername().trim().toLowerCase());
        }
        // Set the user's role based on the role parameter
        if (role.equalsIgnoreCase("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        return user;
    }

    /**
     * Handles requests to the root endpoint ("/").
     * Determines whether to redirect normal users to tests or show the admin dashboard.
     *
     * @param userDetails the currently authenticated user's details injected by Spring Security
     * @param model the model to pass attributes to the view
     * @return the view name for redirection or for displaying the admin home page
     */
    @RequestMapping(value = "/")
    public String viewHomePage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Check if the authenticated user has the ROLE_USER authority
        boolean hasUserRole = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        if (hasUserRole) {
            // Normal users are redirected to the tests page
            return "redirect:/tests";
        }

        // For admin users, gather various statistics to display on the admin dashboard
        Long adminId = userDetails.getId();
        // Count the tests created by the admin
        int testsNumber = testService.findAllByCreaterId(adminId).size();
        // Count the number of active users managed by the admin
        int usersNumber = userService.findAllActiveUsersByAdminId(adminId).size();
        // Find the number of pending registration requests for normal users
        int requestsNumber = findRequestsNumber(adminId);
        // Retrieve the test results and convert them into a list of FormView objects
        List<FormView> results = findResultsFormViewByAdminId(adminId);
        int resultsNumber = results.size();

        // Group the results by month (using a utility method) to display trends over time
        Map<String, Long> restultsInMonths = DateTimeUtils.getResultsInMonths(results);
        Collection<Long> numberOfResultsInMonths = restultsInMonths.values();

        // Add the admin user details and collected statistics to the model
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("testsNumber", testsNumber);
        model.addAttribute("usersNumber", usersNumber);
        model.addAttribute("requestsNumber", requestsNumber);
        model.addAttribute("resultsNumber", resultsNumber);
        model.addAttribute("numberOfResultsInMonths", numberOfResultsInMonths);

        // Return the admin dashboard view name
        return "adminHome";
    }

    /**
     * Finds the number of pending registration requests for normal users.
     * It filters out users who are not yet enabled and have not received a confirmation token.
     *
     * @param adminId the ID of the admin for which to count pending requests
     * @return the number of pending registration requests
     */
    private int findRequestsNumber(Long adminId) {
        // Retrieve all users associated with the admin and filter for non-enabled normal users
        List<User> usersRequests = userService.findAllUsersByAdminId(adminId)
                .stream()
                // Only include users with ROLE_USER who are not enabled
                .filter(user -> !user.isEnabled() && "ROLE_USER".equals(user.getRole()))
                .collect(Collectors.toList());

        // Retrieve users who have already received an unused confirmation token
        List<User> usersReceivedAConfirmMessage = confirmationTokenService.findUsersWithUnusedToken(adminId)
                .stream()
                // Filter again for non-enabled normal users
                .filter(user -> !user.isEnabled() && "ROLE_USER".equals(user.getRole()))
                .collect(Collectors.toList());

        // Remove users who have already been sent a confirmation token from the pending requests
        usersRequests.removeAll(usersReceivedAConfirmMessage);

        // Return the count of users still awaiting confirmation
        return usersRequests.size();
    }

    /**
     * Retrieves test results for an admin and converts raw query results into a list of FormView objects.
     *
     * @param adminId the ID of the admin whose results should be retrieved
     * @return a list of FormView objects representing individual test results
     */
    public List<FormView> findResultsFormViewByAdminId(Long adminId) {
        // Query the database for results associated with the admin; each row is returned as an Object array
        List<Object[]> allResults = resultService.findAllByAdminId(adminId);

        // Prepare a list to store the converted FormView objects
        List<FormView> resultsFormView = new ArrayList<>();
        // Iterate over each row and extract the individual fields
        for (Object[] row : allResults) {
            String username = (String) row[0];
            String testName = (String) row[1];
            Date date = (Date) row[2];
            Double totalMark = (Double) row[3];
            Double grade = (Double) row[4];
            Boolean passed = (Boolean) row[5];
            // Create a new FormView object with the extracted data
            FormView formView = new FormView(username, testName, date, totalMark, grade, passed);
            // Add the FormView to the list
            resultsFormView.add(formView);
        }
        return resultsFormView;
    }
}

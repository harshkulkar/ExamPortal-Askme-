package com.hcoders.portal.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ConfirmationTokenService;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;
import com.hcoders.portal.utils.DateTimeUtils;
@Controller
public class RegistrationController {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("testServiceImpl")
    private TestService testService;

    @Autowired
    @Qualifier("confirmationTokenServiceImpl")
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout, String emailConfirmation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "sign-in-sign-up";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "sign-in-sign-up";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registration(Model model, User user, @Param("role") String role,
                               @Param("adminEmail") String adminEmail) {
        // Check if a user with the given email exists
        User existingUser = userService.findByEmailIgnoreCase(user.getEmail());
        if (existingUser != null) {
            model.addAttribute("message", "This email " + user.getEmail() + " already exists!");
            return "sign-in-sign-up";
        }
        // Check if a user with the given username exists
        existingUser = userService.findByUsernameIgnoreCase(user.getUsername());
        if (existingUser != null) {
            model.addAttribute("message", "This User " + user.getUsername() + " already exists!");
            return "sign-in-sign-up";
        }

        if (role != null && !role.isEmpty() && role.equalsIgnoreCase("admin")) {
            // For admin registrations, use the adminEmail as the confirmation admin's email.
            User confirmationAdmin = userService.findByEmailIgnoreCase(adminEmail);
            if (confirmationAdmin == null) {
                model.addAttribute("message", "No Admin found with the provided email " + adminEmail);
                return "sign-in-sign-up";
            }
            return createAdminUser(model, user, role, confirmationAdmin);
        } else if (role != null && !role.isEmpty() && role.equalsIgnoreCase("user")) {
            // For normal user registrations, an admin's email is also required.
            User admin = userService.findByEmailIgnoreCase(adminEmail);
            if (admin == null) {
                model.addAttribute("message", "An Admin with the given email " + adminEmail + " doesn't exist!");
                return "sign-in-sign-up";
            }
            return createNormalUser(model, user, role, admin);
        }

        return "redirect:/login";
    }

    /**
     * Helper method for normal users.
     * After saving the user, the new user is assigned to the given admin.
     */
    private String createNormalUser(Model model, User user, String role, User admin) {
        user = createUserWithRole(user, role);
        user = userService.save(user);
        userService.assignUser(admin.getId(), user.getId());
        model.addAttribute("emailConfirmation", user.getEmail());
        return "sign-in-sign-up";
    }

    /**
     * Helper method for admin registrations.
     * The new admin account is created and then assigned to an existing admin for confirmation.
     */
    private String createAdminUser(Model model, User user, String role, User confirmationAdmin) {
        user = createUserWithRole(user, role);
        user = userService.save(user);
        // Assign this new admin to the existing admin (confirmationAdmin) for confirmation.
        userService.assignUser(confirmationAdmin.getId(), user.getId());
        model.addAttribute("message", "Admin account created. Awaiting confirmation from " + confirmationAdmin.getEmail());
        return "sign-in-sign-up";
    }

    /**
     * Common helper to create a user with the appropriate role.
     */
    private User createUserWithRole(User user, String role) {
        if (!user.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }
        if (!user.getUsername().trim().isEmpty()) {
            user.setUsername(user.getUsername().trim().toLowerCase());
        }
        // Set role based on the registration type.
        if (role.equalsIgnoreCase("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        return user;
    }

    @RequestMapping(value = "/")
    public String viewHomePage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        boolean hasUserRole = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        if (hasUserRole) {
            return "redirect:/tests";
        }

        Long adminId = userDetails.getId();
        int testsNumber = testService.findAllByCreaterId(adminId).size();
        int usersNumber = userService.findAllActiveUsersByAdminId(adminId).size();
        int requestsNumber = findRequestsNumber(adminId);
        List<FormView> results = findResultsFormViewByAdminId(adminId);
        int resultsNumber = results.size();

        Map<String, Long> restultsInMonths = DateTimeUtils.getResultsInMonths(results);
        Collection<Long> numberOfResultsInMonths = restultsInMonths.values();

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("testsNumber", testsNumber);
        model.addAttribute("usersNumber", usersNumber);
        model.addAttribute("requestsNumber", requestsNumber);
        model.addAttribute("resultsNumber", resultsNumber);
        model.addAttribute("numberOfResultsInMonths", numberOfResultsInMonths);

        return "adminHome";
    }

    /**
     * Updated method to find pending normal-user requests.
     * Only non-enabled users with role ROLE_USER that have not yet received a confirmation token are counted.
     */
    private int findRequestsNumber(Long adminId) {
        List<User> usersRequests = userService.findAllUsersByAdminId(adminId)
                .stream()
                // Consider only normal user requests.
                .filter(user -> !user.isEnabled() && "ROLE_USER".equals(user.getRole()))
                .collect(Collectors.toList());

        List<User> usersReceivedAConfirmMessage = confirmationTokenService.findUsersWithUnusedToken(adminId)
                .stream()
                .filter(user -> !user.isEnabled() && "ROLE_USER".equals(user.getRole()))
                .collect(Collectors.toList());

        // Exclude users who have already received a confirmation token.
        usersRequests.removeAll(usersReceivedAConfirmMessage);

        return usersRequests.size();
    }

    public List<FormView> findResultsFormViewByAdminId(Long adminId) {
        List<Object[]> allResults = resultService.findAllByAdminId(adminId);

        List<FormView> resultsFormView = new ArrayList<>();
        for (Object[] row : allResults) {
            String username = (String) row[0];
            String testName = (String) row[1];
            Date date = (Date) row[2];
            Double totalMark = (Double) row[3];
            Double grade = (Double) row[4];
            Boolean passed = (Boolean) row[5];
            FormView formView = new FormView(username, testName, date, totalMark, grade, passed);
            resultsFormView.add(formView);
        }
        return resultsFormView;
    }
}

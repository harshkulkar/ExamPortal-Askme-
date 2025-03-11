package com.hcoders.portal.controller;

// Import necessary Java utility classes for list handling and stream operations
import java.util.List;
import java.util.stream.Collectors;

// Import Spring Framework classes for dependency injection, security, and web MVC
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Import application-specific classes for the domain model, security details, and services
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ConfirmationTokenService;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.UserService;

@Controller // Marks the class as a Spring MVC Controller that handles HTTP requests
public class UserController {

    // Inject UserService to handle operations related to user management
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    // Inject ResultService to manage operations related to test results
    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService;

    // Inject ConfirmationTokenService to manage confirmation tokens (e.g., for email verification)
    @Autowired
    @Qualifier("confirmationTokenServiceImpl")
    private ConfirmationTokenService confirmationTokenService;

    /**
     * Handles GET requests for the "/users" endpoint.
     * This method retrieves and displays a list of users assigned to the current admin who are enabled.
     *
     * @param userDetails Contains the currently authenticated user's details.
     * @param model       The Model object to pass data to the view.
     * @return            The name of the view template ("users") to render.
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String viewUsersPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Retrieve all users assigned to the current admin by admin ID
        // Then filter the list to include only users that are enabled
        List<User> addedUsers = userService.findAllUsersByAdminId(userDetails.getId())
                .stream()
                .filter(user -> user.isEnabled() == true)
                .collect(Collectors.toList());
        
        // Determine the number of enabled users
        int numberOfUsers = addedUsers.size();
        // Add the count of users to the model for display in the view
        model.addAttribute("numberOfUsers", numberOfUsers);
        // Add the list of enabled users to the model for display in the view
        model.addAttribute("addedUsers", addedUsers);
        // Also add the currently authenticated user details to the model
        model.addAttribute("user", userDetails.getUser());

        // Return the view name "users" so that Spring can render the appropriate template
        return "users";
    }

    /**
     * Handles POST requests for the "/delete-user" endpoint.
     * This method deletes a user, along with their confirmation tokens and test results.
     *
     * @param userId             The ID of the user to be deleted, passed as a request parameter.
     * @param redirectAttributes Used to pass flash attributes for notifications on redirection.
     * @return                   A redirection to the "/users" page.
     */
    @RequestMapping(value = "/delete-user", method = RequestMethod.POST)
    public String deleteUser(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        // Delete any confirmation tokens associated with the user.
        confirmationTokenService.deleteByUserId(userId);
        // Delete any test results associated with the user.
        resultService.deleteByExamineeId(userId);
        // Finally, delete the user from the system.
        userService.deleteById(userId);
    
        // Add a flash attribute to indicate that a delete action was performed.
        redirectAttributes.addFlashAttribute("action", "deleteUser");

        // Redirect back to the "/users" page to reflect the changes.
        return "redirect:/users";
    }
}

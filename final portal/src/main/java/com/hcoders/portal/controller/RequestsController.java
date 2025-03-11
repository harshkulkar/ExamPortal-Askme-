package com.hcoders.portal.controller;

// Importing List for handling collections and Collectors for stream operations
import java.util.List;
import java.util.stream.Collectors;

// Importing Spring annotations and classes for dependency injection, security, and MVC handling
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Importing application-specific classes for models, security, and services
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.UserService;

@Controller // Marks the class as a Spring MVC Controller to handle HTTP requests
public class RequestsController {

    // Injecting the UserService dependency using the specific implementation "userServiceImpl"
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    /**
     * Displays all pending user requests.
     * Pending requests are users assigned to the current admin whose accounts are not yet enabled.
     *
     * @param userDetails Contains the authenticated admin's details.
     * @param model       The Model used to pass attributes to the view.
     * @return            The name of the view template to render (e.g., "requests").
     */
    @RequestMapping(value = "/requests", method = RequestMethod.GET)
    public String viewUserRequests(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Retrieve the current admin's ID from the authenticated user details.
        Long adminId = userDetails.getId();
        
        // Retrieve all users assigned to this admin.
        // Then filter the list to include only those users whose accounts are not enabled.
        List<User> usersRequests = userService.findAllUsersByAdminId(adminId)
                .stream()
                .filter(user -> !user.isEnabled()) // Only include users that are pending activation
                .collect(Collectors.toList());

        // Add the number of pending requests to the model.
        model.addAttribute("numberOfRequests", usersRequests.size());
        // Add the list of pending user requests to the model.
        model.addAttribute("usersRequests", usersRequests);
        // Add a new FormView object to the model (often used for capturing additional form inputs).
        model.addAttribute("formView", new FormView());
        // Add the currently authenticated user (admin) to the model.
        model.addAttribute("user", userDetails.getUser());
        
        // Return the view name for rendering the requests page.
        return "requests";
    }

    /**
     * Deletes a user request by email.
     * This method is triggered when an admin chooses to remove a pending user request.
     *
     * @param email                The email of the user to be deleted.
     * @param redirectAttributes   Used to pass flash attributes to the redirected view.
     * @return                     A redirection to the "/requests" page.
     */
    @RequestMapping(value = "/delete-request")
    public String deleteUser(@RequestParam String email, RedirectAttributes redirectAttributes) {
        // Delete the user from the system based on the provided email.
        userService.deleteByEmail(email);
        // Add a flash attribute to indicate that a deletion action occurred.
        redirectAttributes.addFlashAttribute("action", "deleteRequest");
        // Print a confirmation message to the console (helpful for debugging purposes).
        System.out.println("User with email " + email + " has been deleted.");
        // Redirect back to the requests page to update the list of pending requests.
        return "redirect:/requests";
    }

    /**
     * Confirms a user request by enabling the user's account.
     * This method handles the activation of a pending user.
     *
     * @param toEmail              The email address of the user to be confirmed.
     * @param redirectAttributes   Used to pass flash attributes to the redirected view.
     * @return                     A redirection to the "/requests" page.
     */
    @RequestMapping(value = "/send-confirm", method = RequestMethod.POST)
    public String sendConfirmation(@RequestParam("toEmail") String toEmail,
                                   RedirectAttributes redirectAttributes) {
        // Retrieve the user by email (ignoring case differences).
        User user = userService.findByEmailIgnoreCase(toEmail);
        // Check if the user exists and that their account is not already enabled.
        if (user != null && !user.isEnabled()) {
            // Enable the user's account.
            user.setEnabled(true);
            // Save the updated user details to the database.
            userService.save(user);
            // Add a flash attribute indicating successful confirmation.
            redirectAttributes.addFlashAttribute("confirmed", "User has been successfully enabled.");
            // Print a confirmation message to the console.
            System.out.println("Confirmation sent! User " + toEmail + " has been enabled.");
        } else {
            // If the user is not found or already enabled, add an error message.
            redirectAttributes.addFlashAttribute("error", "User not found or already enabled.");
            // Print an error message to the console.
            System.out.println("Error: User " + toEmail + " not found or already enabled.");
        }
        // Redirect back to the requests page.
        return "redirect:/requests";
    }
}

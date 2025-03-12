package com.hcoders.portal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.UserService;
@Controller
public class RequestsController {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    /**
     * Displays all pending user requests (i.e. users assigned to the current admin who are not enabled).
     */
    @RequestMapping(value = "/requests", method = RequestMethod.GET)
    public String viewUserRequests(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long adminId = userDetails.getId();
        
        // Get all users assigned to this admin who are not yet enabled.
        List<User> usersRequests = userService.findAllUsersByAdminId(adminId)
                .stream()
                .filter(user -> !user.isEnabled())
                .collect(Collectors.toList());

        model.addAttribute("numberOfRequests", usersRequests.size());
        model.addAttribute("usersRequests", usersRequests);
        model.addAttribute("formView", new FormView());
        model.addAttribute("user", userDetails.getUser());
        
        return "requests";
    }

    /**
     * Deletes a user request by email.
     */
    @RequestMapping(value = "/delete-request")
    public String deleteUser(@RequestParam String email, RedirectAttributes redirectAttributes) {
        userService.deleteByEmail(email);
        redirectAttributes.addFlashAttribute("action", "deleteRequest");
        System.out.println("User with email " + email + " has been deleted.");
        return "redirect:/requests";
    }

    /**
     * Confirms a user request by enabling the user account.
     */
    @RequestMapping(value = "/send-confirm", method = RequestMethod.POST)
    public String sendConfirmation(@RequestParam("toEmail") String toEmail,
                                   RedirectAttributes redirectAttributes) {
        User user = userService.findByEmailIgnoreCase(toEmail);
        if (user != null && !user.isEnabled()) {
            user.setEnabled(true);
            userService.save(user);
            redirectAttributes.addFlashAttribute("confirmed", "User has been successfully enabled.");
            System.out.println("Confirmation sent! User " + toEmail + " has been enabled.");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found or already enabled.");
            System.out.println("Error: User " + toEmail + " not found or already enabled.");
        }
        return "redirect:/requests";
    }
}

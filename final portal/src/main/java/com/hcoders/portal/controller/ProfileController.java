package com.hcoders.portal.controller;

// Importing required Spring and application-specific packages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.UserService;

/**
 * ProfileController handles user profile-related actions, such as 
 * viewing and updating profile information.
 */
@Controller // Marks this class as a Spring MVC Controller
public class ProfileController {

	// Injecting the UserService implementation to handle user-related operations
	@Autowired
	@Qualifier("userServiceImpl") // Explicitly specifying the implementation bean to use
	private UserService userService;

	// Injecting PasswordEncoder for securely hashing passwords
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Handles GET request to "/profile".
	 * Displays the profile page with user details.
	 * 
	 * @param userDetails - Authenticated user's details
	 * @param model - Model object to pass data to the view
	 * @return Name of the view to be rendered ("profile.html")
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String viewProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		// Retrieving the currently logged-in user's details
		User user = userDetails.getUser();
		// Adding the user object to the model so it can be used in the view
		model.addAttribute("user", user);
		// Returning the name of the Thymeleaf template to render
		return "profile";
	}

	/**
	 * Handles POST request to "/update-profile".
	 * Updates user profile details and redirects back to the profile page.
	 * 
	 * @param userDetails - Authenticated user's details
	 * @param user - User object containing updated information
	 * @param redirectAttributes - Redirect attributes to pass messages
	 * @return Redirects to the profile page after update
	 */
	@RequestMapping(value = "/update-profile", method = RequestMethod.POST)
	public String updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, 
			@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {

		// Retrieving the currently logged-in user to modify their details
		User userToBeEdited = userDetails.getUser();

		// Updating only non-empty fields to prevent overwriting with blank values
		if (!user.getFirstName().trim().isEmpty())
			userToBeEdited.setFirstName(user.getFirstName());

		if (!user.getLastName().trim().isEmpty())
			userToBeEdited.setLastName(user.getLastName());

		if (!user.getEmail().trim().isEmpty())
			userToBeEdited.setEmail(user.getEmail());

		// Encoding password before saving to ensure security
		if (!user.getPassword().isEmpty())
			userToBeEdited.setPassword(passwordEncoder.encode(user.getPassword()));

		if (!user.getInstitution().trim().isEmpty())
			userToBeEdited.setInstitution(user.getInstitution());

		// Updating user address details
		if (!user.getAddress().getCity().trim().isEmpty())
			userToBeEdited.getAddress().setCity(user.getAddress().getCity());

		if (!user.getAddress().getStreet().trim().isEmpty())
			userToBeEdited.getAddress().setStreet(user.getAddress().getStreet());

		if (!user.getAddress().getCountry().trim().isEmpty())
			userToBeEdited.getAddress().setCountry(user.getAddress().getCountry());

		// Ensuring postal code is valid before updating
		if (user.getAddress().getPostalCode() > 0)
			userToBeEdited.getAddress().setPostalCode(user.getAddress().getPostalCode());

		// Saving the updated user details in the database
		userService.update(userToBeEdited);

		// Adding a flash attribute to indicate profile update action
		redirectAttributes.addFlashAttribute("action", "updateProfile");

		// Redirecting back to the profile page after update
		return "redirect:/profile";
	}
}

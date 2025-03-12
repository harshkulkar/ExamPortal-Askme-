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

import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ConfirmationTokenService;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.UserService;


@Controller
public class UserController {

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("resultServiceImpl")
	private ResultService resultService;

	@Autowired
	@Qualifier("confirmationTokenServiceImpl")
	private ConfirmationTokenService confirmationTokenService;

	/**
	 *
	 *
	 * @param userDetails
	 * @param model
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String viewUsersPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		List<User> addedUsers = userService.findAllUsersByAdminId(userDetails.getId()).stream()
				.filter(user -> user.isEnabled() == true).collect(Collectors.toList());
		int numberOfUsers = addedUsers.size();
		model.addAttribute("numberOfUsers", numberOfUsers);
		model.addAttribute("addedUsers", addedUsers);
		model.addAttribute("user", userDetails.getUser());

		return "users";
	}

	/**
	 *
	 *
	 * @param userId
	 * @param redirectAttributes
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	@RequestMapping(value = "/delete-user",  method = RequestMethod.POST)
	public String deleteUser(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
		confirmationTokenService.deleteByUserId(userId);
		resultService.deleteByExamineeId(userId);
		userService.deleteById(userId);
	
		redirectAttributes.addFlashAttribute("action", "deleteUser");

		return "redirect:/users";
	}

}

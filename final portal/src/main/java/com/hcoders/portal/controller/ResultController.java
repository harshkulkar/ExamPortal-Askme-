package com.hcoders.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.Result;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ResultService;



@Controller
public class ResultController {

	@Autowired
	@Qualifier("resultServiceImpl")
	private ResultService resultService;

	/**
	 *
	 *
	 * @param userDetails
	 * @param model
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String viewTestsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		if (userDetails.getUser().getRole().equals("ROLE_USER")) {
			List<Result> resultsForNormalUser = resultService.findByExamineeId(userDetails.getId());
			int numberForUserResults = resultsForNormalUser.size();
			model.addAttribute("numberForUserResults", numberForUserResults);
			model.addAttribute("resultsForNormalUser", resultsForNormalUser);

		}

		List<FormView> resultsFormView = finResultsByAdminId(userDetails.getId());

		int numberForAdminResults = resultsFormView.size();
		model.addAttribute("numberForAdminResults", numberForAdminResults);
		model.addAttribute("resultsFormView", resultsFormView);
		model.addAttribute("user", userDetails.getUser());

		return "results";
	}

	/**
	 *
	 *
	 * @param adminId
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	public List<FormView> finResultsByAdminId(Long adminId) {
		List<Object[]> allResults = resultService.findAllByAdminId(adminId);

		List<FormView> resultsFormView = new ArrayList<FormView>();
		for (int i = 0; i < allResults.size(); i++) {
			FormView formView = createFormViewForResult(allResults, i);
			resultsFormView.add(formView);
		}
		return resultsFormView;
	}

	/**
	 *
	 *
	 * @param allResults
	 * @param i
	 * @return
	 *
	 * @author Feras Ejneid
	 */
	private FormView createFormViewForResult(List<Object[]> allResults, int i) {
		String username = (String) allResults.get(i)[0];
		String testName = (String) allResults.get(i)[1];
		Date date = (Date) allResults.get(i)[2];
		Double totalMark = (Double) allResults.get(i)[3];
		Double grade = (Double) allResults.get(i)[4];
		Boolean passed = (Boolean) allResults.get(i)[5];
		FormView formView = new FormView(username, testName, date, totalMark, grade, passed);
		return formView;
	}
}

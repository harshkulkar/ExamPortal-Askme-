package com.hcoders.portal.controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcoders.portal.model.Answer;
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.Question;
import com.hcoders.portal.model.QuestionWithSelectionListWrapper;
import com.hcoders.portal.model.Result;
import com.hcoders.portal.model.Test;
import com.hcoders.portal.model.User;
import com.hcoders.portal.model.VerificationCode;
import com.hcoders.portal.repository.VerificationCodeRepository;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.QuestionService;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;





@Controller
public class TestController {

	@Autowired
	@Qualifier("testServiceImpl")
	private TestService testService;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("questionServiceImpl")
	private QuestionService questionService;

	@Autowired
	@Qualifier("resultServiceImpl")
	private ResultService resultService;
	
	@Autowired
	private VerificationCodeRepository verificationCodeRepository;
	
	@Autowired
	private MailSender mailSender;
	


	@RequestMapping(value = "/tests", method = RequestMethod.GET)
	public String viewTestsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		Long id = userDetails.getId();
		List<Test> tests = testService.findAllByCreaterId(id);
		if (userDetails.getUser().getRole().equals("ROLE_USER"))
			tests = findAvailableUserTests(userDetails, id);

		FormView formView = new FormView();

		for (Test test : tests) {
			test.setUsersTobeTested(userService.findAllCandidatesForATestByAdminId(test.getId(), userDetails.getId()));
			formView.getTestsView().add(test);
		}

		Test test = new Test();
		model.addAttribute("test", test);
		model.addAttribute("tests", tests);
		model.addAttribute("formView", formView);
		model.addAttribute("user", userDetails.getUser());
		

		return "tests";
	}
	
	@RequestMapping(value = "/tests-list", method = RequestMethod.GET)
	public  @ResponseBody List<Test> getTests(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		Long id = userDetails.getId();
		List<Test> tests = testService.findAllByCreaterId(id);
		if (userDetails.getUser().getRole().equals("ROLE_USER"))
			tests = findAvailableUserTests(userDetails, id);

		FormView formView = new FormView();

		for (Test test : tests) {
			test.setUsersTobeTested(userService.findAllCandidatesForATestByAdminId(test.getId(), userDetails.getId()));
			formView.getTestsView().add(test);
		}

		Test test = new Test();
		model.addAttribute("test", test);
		model.addAttribute("tests", tests);
		model.addAttribute("formView", formView);

		return tests;
	}



	private List<Test> findAvailableUserTests(UserDetailsImpl userDetails, Long id) {
		List<Test> tests;
		id = userService.findAdminOf(userDetails.getId());
		tests = testService.findAllByCreaterId(id);
		List<Test> testToRemove = new ArrayList<Test>();
		for (Test test : tests) {
			boolean testAccess = testService.isAccessableByUser(test.getId(), userDetails.getId());
			if (!testAccess)
				testToRemove.add(test);
		}
		tests.removeAll(testToRemove);
		return tests;
	}


	@RequestMapping(value = "/add-test", method = RequestMethod.POST)
	public String saveExam(@AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute("test") Test test,
			RedirectAttributes redirectAttributes) {
		Long id = userDetails.getId();
		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			test.setTestCreator(user.get());
			testService.addTest(test);
		}

		redirectAttributes.addFlashAttribute("notification", "New Test successfully saved");
		redirectAttributes.addFlashAttribute("action", "saveTest");

		return "redirect:/tests";
	}

	@RequestMapping(value = "/edit-test", method = RequestMethod.POST)
	public String editExam(@ModelAttribute("test") Test test, RedirectAttributes redirectAttributes) {
		Optional<Test> foundTest = testService.findById(test.getId());
		if (foundTest.isPresent()) {
			foundTest.get().setName(test.getName());
			foundTest.get().setTime(test.getTime());
			foundTest.get().setModifyDate(LocalDateTime.now());
			testService.addTest(foundTest.get());
		}

		redirectAttributes.addFlashAttribute("notification", "Test was successfully updated");
		redirectAttributes.addFlashAttribute("action", "updateTest");
		return "redirect:/tests";
	}


	@RequestMapping(value = "/delete-test")
	public String deleteTest(@ModelAttribute("test") Test test, RedirectAttributes redirectAttributes) {
		testService.deleteById(test.getId());
		
		redirectAttributes.addFlashAttribute("notification", "Test was successfully deleted");
		redirectAttributes.addFlashAttribute("action", "deleteTest");
		return "redirect:/tests";
	}


	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String startTest(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
			@RequestParam(value = "testId") Long testId) {
		Optional<Test> test = testService.findById(testId);

		if (test.isPresent()) {
			test.get().getUsersTobeTested().add(userDetails.getUser());
			testService.save(test.get());
			ArrayList<Question> questions = (ArrayList<Question>) questionService.findByTestId(testId);
			QuestionWithSelectionListWrapper wrapper = new QuestionWithSelectionListWrapper();
			wrapper.setQuestionsList(questions);
			model.addAttribute("wrapper", wrapper);
			model.addAttribute("testId", testId);
			model.addAttribute("test", test.get());
		}
		Long examineeId = userDetails.getId();
		testService.setTestAccess(testId, examineeId, 0);
		return "test";
	}
	
	
	@PostMapping(value = "/verifyCode", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> verifyCode(
	        @AuthenticationPrincipal UserDetailsImpl userDetails,
	        @RequestBody Map<String, Object> payload) {

	    Map<String, Object> response = new HashMap<>();
	    
	    Long testId = Long.valueOf(payload.get("testId").toString());
	    String enteredCode = payload.get("code").toString().trim(); // Trim any whitespace

	    // Fetch all verification codes for the user and test
	    List<VerificationCode> verificationCodes =
	        verificationCodeRepository.findByUserIdAndTestId(userDetails.getId(), testId);

	    // Debugging: Log retrieved codes
	    System.out.println("Retrieved Verification Codes:");
	    for (VerificationCode vc : verificationCodes) {
	        System.out.println("ID: " + vc.getId() + ", Code: " + vc.getCode() + ", Expiry: ");
	    }

	    // If no verification codes exist, return false
	    if (verificationCodes.isEmpty()) {
	        response.put("verified", false);
	        response.put("message", "No verification code found.");
	        return response;
	    }

	    // Check if any stored code matches the entered code and is not expired
	    boolean isVerified = verificationCodes.stream()
	            .anyMatch(vc -> vc.getCode().trim().equals(enteredCode));

	    // Debugging: Log validation results
	    System.out.println("Entered Code: " + enteredCode);
	    System.out.println("Validation Result: " + isVerified);

	    response.put("verified", isVerified);
	    response.put("message", isVerified ? "Verification successful!" : "Invalid or expired code.");
	    return response;
	}


	
	


	@RequestMapping(value = "/finish-test", method = RequestMethod.POST)
	public String finishTest(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute("wrapper") QuestionWithSelectionListWrapper wrapper) {
		List<Question> questionsWithSelectedAnswers = wrapper.getQuestionsList();
		Long examineeId = userDetails.getId();
		Test takenTest = questionsWithSelectedAnswers.get(0).getTest();
		Result result = createResult(takenTest, questionsWithSelectedAnswers, examineeId);
		resultService.save(result);
		testService.setTestAccess(takenTest.getId(), examineeId, 0);

		return "redirect:/tests";
	}


	private Result createResult(Test takenTest, List<Question> questionsWithSelectedAnswers, Long examineeId) {
		double fullPoints = 0.0;
		double grade = 0.0;
		for (Question question : questionsWithSelectedAnswers) {
			List<Answer> answers = question.getAnswers();
			boolean containsWrongAnswers = answers.stream().anyMatch(
					answer -> answer.isSelected() && !answer.isCorrect() || answer.isCorrect() && !answer.isSelected());
			if (!containsWrongAnswers)
				grade += question.getPoints();

			fullPoints += question.getPoints();
		}

		Long testId = takenTest.getId();
		String testName = takenTest.getName();
		double totalMark = takenTest.getTotalMark();
		boolean passed = grade >= (fullPoints * 0.5);

		Result result = new Result(testId, testName, grade, totalMark, passed, examineeId);
		return result;
	}

	@RequestMapping(value = "/add-candidate", method = RequestMethod.POST)
	public String addCandidate(@RequestParam(value = "userId") Long userId, 
	                           @RequestParam(value = "testId") Long testId,
	                           RedirectAttributes redirectAttributes) {
	    
	    Optional<User> userFromDb = userService.findById(userId);

	    if (userFromDb.isPresent()) {
	        // Assign test access
	        testService.setTestAccess(testId, userId, 1);

	        // Generate Verification Code
	        String verificationCode = generateVerificationCode();

	        // Store Code in DB
	        VerificationCode verification = new VerificationCode();
	        verification.setUserId(userId);
	        verification.setTestId(testId);
	        verification.setCode(verificationCode);
//	        verification.setExpiry(LocalDateTime.now().plusMinutes(10)); // Expires in 10 minutes
	        verificationCodeRepository.save(verification);

	        // Send Email with Verification Code
	        sendVerificationCode(userFromDb.get().getEmail(), verificationCode);
	    }

	    redirectAttributes.addAttribute("testId", testId);
	    return "redirect:/questions";
	}

	// Generate a 6-digit verification code
	private String generateVerificationCode() {
	    return String.format("%06d", new Random().nextInt(999999));
	}

	// Send email with the verification code
	private void sendVerificationCode(String email, String code) {
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(email);
	    message.setSubject("Your Exam Verification Code");
	    message.setText("Your verification code is: " + code);
	    mailSender.send(message);
	}



}

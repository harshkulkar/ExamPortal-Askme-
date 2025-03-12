package com.hcoders.portal.controller;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcoders.portal.model.Answer;
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.Question;
import com.hcoders.portal.model.Test;
import com.hcoders.portal.model.User;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.AnswerService;
import com.hcoders.portal.service.QuestionService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;



@Controller
public class QuestionController {

	@Autowired
	@Qualifier("testServiceImpl")
	private TestService testService;

	@Autowired
	@Qualifier("questionServiceImpl")
	private QuestionService questionService;

	@Autowired
	@Qualifier("answerServiceImpl")
	private AnswerService answerService;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;


	/*  Handles GET requests for the "/questions" endpoint.
	 * This method retrieves a test, its questions, and additional information to display
	 * on the questions page.
	 */
	@RequestMapping(value = "/questions", method = RequestMethod.GET)
	public String viewQuestions(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
			@RequestParam(value = "testId") Long testId) {
		Optional<Test> test = testService.findById(testId);
		List<Question> questions = questionService.findByTestId(testId);

		ArrayList<String> correctNumbers = new ArrayList<String>();
		correctNumbers.add("1");
		correctNumbers.add("2");
		correctNumbers.add("3");
		correctNumbers.add("4");

		model.addAttribute("question", new Question());
		model.addAttribute("option1", new Answer());
		model.addAttribute("option2", new Answer());
		model.addAttribute("option3", new Answer());
		model.addAttribute("option4", new Answer());
		model.addAttribute("correctNumbers", correctNumbers);
		model.addAttribute("questions", questions);
		model.addAttribute("formView", new FormView());
		if (test.isPresent()) {
			List<User> candidates = userService.findAllCandidatesForATestByAdminId(testId, userDetails.getId());
			model.addAttribute("user", userDetails.getUser());
			model.addAttribute("candidates", candidates);
			model.addAttribute("testId", testId);
			model.addAttribute("test", test.get());
		}

		return "questions";
	}

	
	//Handles post request for adding questions
	@RequestMapping(value = "/add-question", method = RequestMethod.POST)
	public String saveQuestion(@ModelAttribute("question") Question question,
			@ModelAttribute("formView") FormView formView, @RequestParam Long testId,
			RedirectAttributes redirectAttributes) {
		Optional<Test> test = testService.findById(testId);

		if (test.isPresent()) {
			Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());
			if (!questionFromDb.isPresent()) {
				saveAnswers(question, formView);
				updateTest(question, testId, redirectAttributes, test);
				redirectAttributes.addFlashAttribute("action", "saveQuestion");
			}
		}
		
		
		return "redirect:/questions";
	}

	
	//Updates the test from newly added test, by adding newly added questions.
	private void updateTest(Question question, Long testId, RedirectAttributes redirectAttributes,
			Optional<Test> test) {
		double roundenQuestionPoints = saveQuestion(question, test);
		redirectAttributes.addAttribute("testId", testId);

		double testTotalMark = test.get().getTotalMark() + roundenQuestionPoints;
		test.get().setTotalMark(testTotalMark);
		testService.setTotalMarkForTest(testTotalMark, testId);
	}

	private double saveQuestion(Question question, Optional<Test> test) {
		question.setTest(test.get());

		double roundenQuestionPoints = Math.round(question.getPoints() * 10) / 10.0;
		question.setPoints(roundenQuestionPoints);
		questionService.save(question);
		return roundenQuestionPoints;
	}

	
	private void saveAnswers(Question question, FormView formView) {
		List<Answer> answers = question.getAnswers();
		int[] correctAnswers = formView.getCorrectAnswers();
		for (int i = 0; i < answers.size(); i++)
			for (int j = 0; j < correctAnswers.length; j++)
				if (i + 1 == correctAnswers[j])
					answers.get(i).setCorrect(true);

		for (Answer answer : answers)
			answer.setQuestion(question);
		// to avoid ConcurrentModificationException
		List<Answer> answersCopy = answers;
		for (int i = 0; i < answersCopy.size(); i++) {
			answerService.saveAndFlush(answers.get(i));
		}
	}

	
	@RequestMapping(value = "/edit-question", method = RequestMethod.POST)
	public String editQuestion(@ModelAttribute("question") Question question,
			@ModelAttribute("formView") FormView formView, @RequestParam Long testId,
			RedirectAttributes redirectAttributes) {

		Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());

		if (questionFromDb.isPresent()) {
			updateAnswers(question, formView);
			questionFromDb.get().setText(question.getText());
			questionFromDb.get().setPoints(question.getPoints());
			
			questionService.save(questionFromDb.get());
			redirectAttributes.addAttribute("testId", testId);
			redirectAttributes.addFlashAttribute("action", "updateQuestion");
		}

		return "redirect:/questions";
	}

	
	private void updateAnswers(Question question, FormView formView) {
		List<Answer> answers = question.getAnswers();
		int[] correctAnswers = formView.getCorrectAnswers();
		for (int i = 0; i < answers.size(); i++)
			for (int j = 0; j < correctAnswers.length; j++)
				if (i + 1 == correctAnswers[j])
					answers.get(i).setCorrect(true);

		for (int i = 0; i < answers.size(); i++) {
			Optional<Answer> answerFromDb = answerService.findById(answers.get(i).getAnswerId());
			if (answerFromDb.isPresent()) {
				answerFromDb.get().setText(answers.get(i).getText());
				answerFromDb.get().setCorrect(answers.get(i).isCorrect());
				answerService.save(answerFromDb.get());
			}
		}
	}

	
	@RequestMapping(value = "/delete-question",  method = RequestMethod.POST)
	public String deleteQuestion(@ModelAttribute("question") Question question,
			RedirectAttributes redirectAttributes) {
		Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());
		deleteAnswers(questionFromDb);
		questionService.deleteById(questionFromDb.get().getQuestionId());
		redirectAttributes.addAttribute("testId", questionFromDb.get().getTest().getId());
		redirectAttributes.addFlashAttribute("action", "deleteQuestion");
		return "redirect:/questions";
	}

	
	private void deleteAnswers(Optional<Question> questionFromDb) {
		List<Answer> answers = questionFromDb.get().getAnswers();

		for (Answer answer : answers) {
			answer.setQuestion(null);
			answerService.deleteById(answer.getAnswerId());
		}
	}

}

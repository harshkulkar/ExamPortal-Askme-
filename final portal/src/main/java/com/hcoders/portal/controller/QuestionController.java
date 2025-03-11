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

@Controller // Indicates that this class is a Spring MVC Controller
public class QuestionController {

	// Inject the TestService implementation to manage Test-related operations
	@Autowired
	@Qualifier("testServiceImpl")
	private TestService testService;

	// Inject the QuestionService implementation to manage Question-related operations
	@Autowired
	@Qualifier("questionServiceImpl")
	private QuestionService questionService;

	// Inject the AnswerService implementation to manage Answer-related operations
	@Autowired
	@Qualifier("answerServiceImpl")
	private AnswerService answerService;

	// Inject the UserService implementation to manage User-related operations
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	/**
	 * Handles GET requests for the "/questions" endpoint.
	 * This method retrieves a test, its questions, and additional information to display
	 * on the questions page.
	 *
	 * @param userDetails - Holds the authenticated user's details
	 * @param model - Model to pass attributes to the view (template)
	 * @param testId - The unique identifier for the test
	 * @return the name of the view template ("questions")
	 */
	@RequestMapping(value = "/questions", method = RequestMethod.GET)
	public String viewQuestions(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
			@RequestParam(value = "testId") Long testId) {
		// Retrieve the Test entity by its ID
		Optional<Test> test = testService.findById(testId);
		// Retrieve all questions that belong to the specified test
		List<Question> questions = questionService.findByTestId(testId);

		// Create a list of string values representing possible correct answer positions
		ArrayList<String> correctNumbers = new ArrayList<String>();
		correctNumbers.add("1");
		correctNumbers.add("2");
		correctNumbers.add("3");
		correctNumbers.add("4");

		// Add an empty Question object to the model for form binding in the view
		model.addAttribute("question", new Question());
		// Add empty Answer objects to the model for each answer option on the form
		model.addAttribute("option1", new Answer());
		model.addAttribute("option2", new Answer());
		model.addAttribute("option3", new Answer());
		model.addAttribute("option4", new Answer());
		// Pass the list of correct answer options to the view
		model.addAttribute("correctNumbers", correctNumbers);
		// Add the list of retrieved questions to the model for display
		model.addAttribute("questions", questions);
		// Add a FormView object to capture additional form data (e.g., correct answers)
		model.addAttribute("formView", new FormView());
		
		// If the test exists, fetch additional information to display
		if (test.isPresent()) {
			// Get a list of candidate users associated with the test, filtered by the admin's ID
			List<User> candidates = userService.findAllCandidatesForATestByAdminId(testId, userDetails.getId());
			// Add the logged-in user to the model
			model.addAttribute("user", userDetails.getUser());
			// Add the list of candidates to the model
			model.addAttribute("candidates", candidates);
			// Add the test ID and the Test object to the model for further reference
			model.addAttribute("testId", testId);
			model.addAttribute("test", test.get());
		}

		// Return the name of the view template to render ("questions.html" or similar)
		return "questions";
	}

	/**
	 * Handles POST requests for adding a new question to a test.
	 * It saves the new question along with its answers and updates the associated test's total mark.
	 *
	 * @param question - The Question object bound from the submitted form data
	 * @param formView - Additional form data containing the correct answers information
	 * @param testId - The unique identifier for the test to which the question belongs
	 * @param redirectAttributes - Used to pass flash messages or attributes during redirection
	 * @return a redirection to the "/questions" endpoint
	 */
	@RequestMapping(value = "/add-question", method = RequestMethod.POST)
	public String saveQuestion(@ModelAttribute("question") Question question,
			@ModelAttribute("formView") FormView formView, @RequestParam Long testId,
			RedirectAttributes redirectAttributes) {
		// Retrieve the Test entity using the provided testId
		Optional<Test> test = testService.findById(testId);

		// Proceed only if the test exists
		if (test.isPresent()) {
			// Check if the question already exists in the database by its ID
			Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());
			if (!questionFromDb.isPresent()) {
				// Save answers associated with this question using the data from FormView
				saveAnswers(question, formView);
				// Update the Test entity with the new question's information (e.g., total marks)
				updateTest(question, testId, redirectAttributes, test);
				// Add a flash attribute to indicate that a new question has been saved
				redirectAttributes.addFlashAttribute("action", "saveQuestion");
			}
		}
		
		// Redirect the user back to the questions page
		return "redirect:/questions";
	}

	/**
	 * Updates the Test entity by adding the points from the newly added question.
	 *
	 * @param question - The new question object
	 * @param testId - The unique identifier for the test to be updated
	 * @param redirectAttributes - Used to pass attributes during redirection
	 * @param test - The Test entity wrapped in an Optional
	 */
	private void updateTest(Question question, Long testId, RedirectAttributes redirectAttributes,
			Optional<Test> test) {
		// Save the question and get the rounded value of its points
		double roundenQuestionPoints = saveQuestion(question, test);
		// Add testId as a redirect attribute so that it remains available after redirection
		redirectAttributes.addAttribute("testId", testId);

		// Calculate the new total mark for the test by adding the question's points
		double testTotalMark = test.get().getTotalMark() + roundenQuestionPoints;
		test.get().setTotalMark(testTotalMark);
		// Persist the updated total mark for the test in the database
		testService.setTotalMarkForTest(testTotalMark, testId);
	}

	/**
	 * Saves a question to the database by associating it with its Test and rounding its points.
	 *
	 * @param question - The question to be saved
	 * @param test - The Test entity (wrapped in an Optional) to which the question belongs
	 * @return the rounded points value of the question
	 */
	private double saveQuestion(Question question, Optional<Test> test) {
		// Associate the question with its corresponding Test
		question.setTest(test.get());

		// Round the question's points to one decimal place
		double roundenQuestionPoints = Math.round(question.getPoints() * 10) / 10.0;
		question.setPoints(roundenQuestionPoints);
		// Save the question entity to the database
		questionService.save(question);
		// Return the rounded points for use in updating the test's total marks
		return roundenQuestionPoints;
	}

	/**
	 * Saves the answers associated with a question.
	 * It updates the correct flags based on the form input and then persists each answer.
	 *
	 * @param question - The question object containing the list of answers
	 * @param formView - Contains form data specifying which answers are correct
	 */
	private void saveAnswers(Question question, FormView formView) {
		// Retrieve the list of Answer objects associated with the question
		List<Answer> answers = question.getAnswers();
		// Get the array of correct answer numbers from the form data
		int[] correctAnswers = formView.getCorrectAnswers();
		
		// Loop through each answer and mark it as correct if its position matches the form data
		for (int i = 0; i < answers.size(); i++)
			for (int j = 0; j < correctAnswers.length; j++)
				if (i + 1 == correctAnswers[j])
					answers.get(i).setCorrect(true);

		// Associate each answer with the question (set the relationship)
		for (Answer answer : answers)
			answer.setQuestion(question);
		
		// Create a copy of the list to avoid modification issues during iteration
		List<Answer> answersCopy = answers;
		// Save and flush each answer to persist it in the database immediately
		for (int i = 0; i < answersCopy.size(); i++) {
			answerService.saveAndFlush(answers.get(i));
		}
	}

	/**
	 * Handles POST requests for editing an existing question.
	 * This method updates the question's text, points, and its associated answers.
	 *
	 * @param question - The question object containing the updated data from the form
	 * @param formView - Contains updated form data, including which answers are marked correct
	 * @param testId - The unique identifier for the test that the question belongs to
	 * @param redirectAttributes - Used to pass flash messages during redirection
	 * @return a redirection to the "/questions" endpoint
	 */
	@RequestMapping(value = "/edit-question", method = RequestMethod.POST)
	public String editQuestion(@ModelAttribute("question") Question question,
			@ModelAttribute("formView") FormView formView, @RequestParam Long testId,
			RedirectAttributes redirectAttributes) {

		// Retrieve the existing question from the database using its ID
		Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());

		// If the question exists, update its details
		if (questionFromDb.isPresent()) {
			// Update the answers for this question based on the submitted form data
			updateAnswers(question, formView);
			// Update the question text and points
			questionFromDb.get().setText(question.getText());
			questionFromDb.get().setPoints(question.getPoints());
			
			// Save the updated question back to the database
			questionService.save(questionFromDb.get());
			// Add the testId as an attribute for redirection
			redirectAttributes.addAttribute("testId", testId);
			// Add a flash attribute to indicate that the question was updated
			redirectAttributes.addFlashAttribute("action", "updateQuestion");
		}

		// Redirect the user back to the questions page
		return "redirect:/questions";
	}

	/**
	 * Updates the answers for a given question.
	 * It sets the correct flag based on form data and updates each answer's text and correctness in the database.
	 *
	 * @param question - The question containing the updated answers
	 * @param formView - Contains the array of indices for the correct answers
	 */
	private void updateAnswers(Question question, FormView formView) {
		// Retrieve the list of Answer objects associated with the question
		List<Answer> answers = question.getAnswers();
		// Retrieve the indices of correct answers from the form data
		int[] correctAnswers = formView.getCorrectAnswers();

		// Loop through each answer and mark it as correct if its position matches one of the correct indices
		for (int i = 0; i < answers.size(); i++)
			for (int j = 0; j < correctAnswers.length; j++)
				if (i + 1 == correctAnswers[j])
					answers.get(i).setCorrect(true);

		// Update each answer in the database with the new text and correctness status
		for (int i = 0; i < answers.size(); i++) {
			// Retrieve the current answer from the database
			Optional<Answer> answerFromDb = answerService.findById(answers.get(i).getAnswerId());
			if (answerFromDb.isPresent()) {
				// Update the answer's text and whether it is marked as correct
				answerFromDb.get().setText(answers.get(i).getText());
				answerFromDb.get().setCorrect(answers.get(i).isCorrect());
				// Save the updated answer back to the database
				answerService.save(answerFromDb.get());
			}
		}
	}

	/**
	 * Handles POST requests for deleting a question.
	 * It first deletes all answers associated with the question to avoid orphan records,
	 * then deletes the question itself.
	 *
	 * @param question - The question to be deleted, bound from the form data
	 * @param redirectAttributes - Used to pass flash messages during redirection
	 * @return a redirection to the "/questions" endpoint
	 */
	@RequestMapping(value = "/delete-question",  method = RequestMethod.POST)
	public String deleteQuestion(@ModelAttribute("question") Question question,
			RedirectAttributes redirectAttributes) {
		// Retrieve the question to be deleted from the database using its ID
		Optional<Question> questionFromDb = questionService.findById(question.getQuestionId());
		// Delete all associated answers first
		deleteAnswers(questionFromDb);
		// Delete the question itself from the database
		questionService.deleteById(questionFromDb.get().getQuestionId());
		// Add the testId as a redirect attribute and a flash attribute to indicate deletion action
		redirectAttributes.addAttribute("testId", questionFromDb.get().getTest().getId());
		redirectAttributes.addFlashAttribute("action", "deleteQuestion");
		// Redirect back to the questions page
		return "redirect:/questions";
	}

	/**
	 * Deletes all answers associated with a given question.
	 *
	 * @param questionFromDb - An Optional containing the question whose answers need to be deleted
	 */
	private void deleteAnswers(Optional<Question> questionFromDb) {
		// Retrieve the list of answers associated with the question
		List<Answer> answers = questionFromDb.get().getAnswers();

		// Loop through each answer and remove its association with the question, then delete it
		for (Answer answer : answers) {
			answer.setQuestion(null);
			answerService.deleteById(answer.getAnswerId());
		}
	}
}

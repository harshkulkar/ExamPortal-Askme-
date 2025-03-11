package com.hcoders.portal.controller;

// Importing necessary Java classes for handling dates, collections, and randomness
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

// Importing Spring Framework classes for dependency injection, security, web MVC, and REST handling
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

// Importing application-specific classes for our domain models and services
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

@Controller // Indicates that this class is a Spring MVC Controller that handles HTTP requests.
public class TestController {

    // Injecting the TestService to handle operations related to tests.
    @Autowired
    @Qualifier("testServiceImpl")
    private TestService testService;

    // Injecting the UserService for operations related to user management.
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    // Injecting the QuestionService for handling question-related functionality.
    @Autowired
    @Qualifier("questionServiceImpl")
    private QuestionService questionService;

    // Injecting the ResultService to manage exam results.
    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService;
    
    // Injecting the repository to store and retrieve verification codes from the database.
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    
    // Injecting the MailSender for sending emails (e.g., verification codes).
    @Autowired
    private MailSender mailSender;
    
    /**
     * Handles GET requests to "/tests".
     * This method renders the tests page with a list of tests.
     * Depending on the user's role, it filters tests accordingly.
     *
     * @param userDetails Contains the authenticated user's details.
     * @param model       The Model used to pass data to the view.
     * @return            The name of the view template to render ("tests").
     */
    @RequestMapping(value = "/tests", method = RequestMethod.GET)
    public String viewTestsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Get the current user's ID from the security context.
        Long id = userDetails.getId();
        // Retrieve tests created by this user (or admin).
        List<Test> tests = testService.findAllByCreaterId(id);
        
        // If the user is a normal user, find only the tests available to them.
        if (userDetails.getUser().getRole().equals("ROLE_USER"))
            tests = findAvailableUserTests(userDetails, id);

        // Create a FormView object to help bind test-related data in the view.
        FormView formView = new FormView();

        // For each test, set the list of users who are to be tested
        // (i.e., candidates assigned to the test by an admin).
        for (Test test : tests) {
            test.setUsersTobeTested(userService.findAllCandidatesForATestByAdminId(test.getId(), userDetails.getId()));
            // Also add the test to the FormView for rendering purposes.
            formView.getTestsView().add(test);
        }

        // Create an empty Test object for form binding (e.g., for adding a new test).
        Test test = new Test();
        // Add attributes to the model so they can be used in the view.
        model.addAttribute("test", test);
        model.addAttribute("tests", tests);
        model.addAttribute("formView", formView);
        model.addAttribute("user", userDetails.getUser());
        
        // Return the name of the view template ("tests").
        return "tests";
    }
    
    /**
     * Returns a list of tests as a JSON response.
     * This endpoint is useful for AJAX requests where test data is needed dynamically.
     *
     * @param userDetails Contains the authenticated user's details.
     * @param model       The Model used to pass data to the view.
     * @return            A list of Test objects.
     */
    @RequestMapping(value = "/tests-list", method = RequestMethod.GET)
    public  @ResponseBody List<Test> getTests(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Get the current user's ID.
        Long id = userDetails.getId();
        // Retrieve tests created by this user.
        List<Test> tests = testService.findAllByCreaterId(id);
        // For normal users, filter tests to only those available to them.
        if (userDetails.getUser().getRole().equals("ROLE_USER"))
            tests = findAvailableUserTests(userDetails, id);

        // Create a FormView and set test data (similar to the viewTestsPage method).
        FormView formView = new FormView();
        for (Test test : tests) {
            test.setUsersTobeTested(userService.findAllCandidatesForATestByAdminId(test.getId(), userDetails.getId()));
            formView.getTestsView().add(test);
        }
        // Create an empty Test object for form binding.
        Test test = new Test();
        model.addAttribute("test", test);
        model.addAttribute("tests", tests);
        model.addAttribute("formView", formView);

        // Return the list of tests (automatically converted to JSON).
        return tests;
    }

    /**
     * Finds tests available to a normal user.
     * It retrieves tests created by the admin of the user and removes tests the user is not allowed to access.
     *
     * @param userDetails The details of the currently authenticated user.
     * @param id          The current user's ID.
     * @return            A list of tests available for the user.
     */
    private List<Test> findAvailableUserTests(UserDetailsImpl userDetails, Long id) {
        List<Test> tests;
        // Find the admin of the current user.
        id = userService.findAdminOf(userDetails.getId());
        // Retrieve all tests created by this admin.
        tests = testService.findAllByCreaterId(id);
        // Create a list to store tests that the user should not access.
        List<Test> testToRemove = new ArrayList<Test>();
        for (Test test : tests) {
            // Check if the test is accessible by the user.
            boolean testAccess = testService.isAccessableByUser(test.getId(), userDetails.getId());
            if (!testAccess)
                testToRemove.add(test);
        }
        // Remove tests that are not accessible.
        tests.removeAll(testToRemove);
        return tests;
    }

    /**
     * Handles POST requests to add a new test.
     * Saves the new test and associates it with the current user as the test creator.
     *
     * @param userDetails        Contains the authenticated user's details.
     * @param test               The Test object populated from the form.
     * @param redirectAttributes Used to pass flash messages upon redirection.
     * @return                   A redirection to the tests page.
     */
    @RequestMapping(value = "/add-test", method = RequestMethod.POST)
    public String saveExam(@AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute("test") Test test,
            RedirectAttributes redirectAttributes) {
        // Get the current user's ID.
        Long id = userDetails.getId();
        // Retrieve the user from the database.
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            // Set the current user as the creator of the test.
            test.setTestCreator(user.get());
            // Save the new test.
            testService.addTest(test);
        }

        // Add flash attributes to notify the user of the successful action.
        redirectAttributes.addFlashAttribute("notification", "New Test successfully saved");
        redirectAttributes.addFlashAttribute("action", "saveTest");

        // Redirect back to the tests page.
        return "redirect:/tests";
    }

    /**
     * Handles POST requests to edit an existing test.
     * Updates the test's name, time, and modification date.
     *
     * @param test               The Test object with updated data from the form.
     * @param redirectAttributes Used to pass flash messages upon redirection.
     * @return                   A redirection to the tests page.
     */
    @RequestMapping(value = "/edit-test", method = RequestMethod.POST)
    public String editExam(@ModelAttribute("test") Test test, RedirectAttributes redirectAttributes) {
        // Find the existing test from the database.
        Optional<Test> foundTest = testService.findById(test.getId());
        if (foundTest.isPresent()) {
            // Update test properties.
            foundTest.get().setName(test.getName());
            foundTest.get().setTime(test.getTime());
            // Set the modification date to the current time.
            foundTest.get().setModifyDate(LocalDateTime.now());
            // Save the updated test.
            testService.addTest(foundTest.get());
        }

        // Add flash attributes to indicate successful update.
        redirectAttributes.addFlashAttribute("notification", "Test was successfully updated");
        redirectAttributes.addFlashAttribute("action", "updateTest");
        return "redirect:/tests";
    }

    /**
     * Handles requests to delete a test.
     *
     * @param test               The Test object to delete (bound from the form).
     * @param redirectAttributes Used to pass flash messages upon redirection.
     * @return                   A redirection to the tests page.
     */
    @RequestMapping(value = "/delete-test")
    public String deleteTest(@ModelAttribute("test") Test test, RedirectAttributes redirectAttributes) {
        // Delete the test by its ID.
        testService.deleteById(test.getId());
        
        // Add flash attributes to notify that the test has been deleted.
        redirectAttributes.addFlashAttribute("notification", "Test was successfully deleted");
        redirectAttributes.addFlashAttribute("action", "deleteTest");
        return "redirect:/tests";
    }

    /**
     * Handles POST requests to start a test.
     * It assigns the test to the current user and prepares the test view with questions.
     *
     * @param userDetails Contains the authenticated user's details.
     * @param model       The Model used to pass data to the view.
     * @param testId      The ID of the test to be started.
     * @return            The name of the view to render (e.g., "test").
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String startTest(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
            @RequestParam(value = "testId") Long testId) {
        // Retrieve the test from the database.
        Optional<Test> test = testService.findById(testId);

        if (test.isPresent()) {
            // Add the current user to the list of users who will take the test.
            test.get().getUsersTobeTested().add(userDetails.getUser());
            // Save the updated test with the new candidate.
            testService.save(test.get());
            // Retrieve the list of questions for the test.
            ArrayList<Question> questions = (ArrayList<Question>) questionService.findByTestId(testId);
            // Wrap the questions into a wrapper object for form binding.
            QuestionWithSelectionListWrapper wrapper = new QuestionWithSelectionListWrapper();
            wrapper.setQuestionsList(questions);
            // Add attributes to the model for the test view.
            model.addAttribute("wrapper", wrapper);
            model.addAttribute("testId", testId);
            model.addAttribute("test", test.get());
        }
        // Set test access status for the current examinee.
        Long examineeId = userDetails.getId();
        testService.setTestAccess(testId, examineeId, 0);
        return "test";
    }
    
    /**
     * Verifies the code entered by the user during the test.
     * This method consumes JSON data and returns a JSON response indicating the verification status.
     *
     * @param userDetails The authenticated user's details.
     * @param payload     The JSON payload containing the test ID and entered code.
     * @return            A map containing verification status and a message.
     */
    @PostMapping(value = "/verifyCode", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> verifyCode(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Map<String, Object> payload) {

        // Prepare a response map to hold the verification result.
        Map<String, Object> response = new HashMap<>();
        
        // Retrieve the test ID and entered code from the payload.
        Long testId = Long.valueOf(payload.get("testId").toString());
        String enteredCode = payload.get("code").toString().trim(); // Trim any whitespace

        // Fetch all verification codes associated with the current user and the test.
        List<VerificationCode> verificationCodes =
            verificationCodeRepository.findByUserIdAndTestId(userDetails.getId(), testId);

        // Debugging: Log retrieved verification codes.
        System.out.println("Retrieved Verification Codes:");
        for (VerificationCode vc : verificationCodes) {
            System.out.println("ID: " + vc.getId() + ", Code: " + vc.getCode() + ", Expiry: ");
        }

        // If no verification codes are found, respond with a failure message.
        if (verificationCodes.isEmpty()) {
            response.put("verified", false);
            response.put("message", "No verification code found.");
            return response;
        }

        // Check if any of the stored verification codes match the entered code.
        boolean isVerified = verificationCodes.stream()
                .anyMatch(vc -> vc.getCode().trim().equals(enteredCode));

        // Debugging: Log the entered code and the result of the validation.
        System.out.println("Entered Code: " + enteredCode);
        System.out.println("Validation Result: " + isVerified);

        // Prepare the response with the verification result and appropriate message.
        response.put("verified", isVerified);
        response.put("message", isVerified ? "Verification successful!" : "Invalid or expired code.");
        return response;
    }
    
    /**
     * Handles POST requests to finish a test.
     * It calculates the test result based on the selected answers, saves the result,
     * and resets test access for the user.
     *
     * @param userDetails Contains the authenticated user's details.
     * @param wrapper     Contains the list of questions with the user's selected answers.
     * @return            A redirection to the tests page.
     */
    @RequestMapping(value = "/finish-test", method = RequestMethod.POST)
    public String finishTest(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @ModelAttribute("wrapper") QuestionWithSelectionListWrapper wrapper) {
        // Retrieve the list of questions (with user selections) from the wrapper.
        List<Question> questionsWithSelectedAnswers = wrapper.getQuestionsList();
        // Get the examinee's (user's) ID.
        Long examineeId = userDetails.getId();
        // Get the test that was taken (assuming it is associated with the first question).
        Test takenTest = questionsWithSelectedAnswers.get(0).getTest();
        // Create a Result object based on the test and the user's answers.
        Result result = createResult(takenTest, questionsWithSelectedAnswers, examineeId);
        // Save the result.
        resultService.save(result);
        // Reset test access for the user.
        testService.setTestAccess(takenTest.getId(), examineeId, 0);

        return "redirect:/tests";
    }

    /**
     * Calculates and creates a Result object based on the test questions and user responses.
     *
     * @param takenTest                   The test that was taken.
     * @param questionsWithSelectedAnswers The list of questions with the user's selected answers.
     * @param examineeId                  The ID of the user who took the test.
     * @return                            A Result object with calculated grade and pass status.
     */
    private Result createResult(Test takenTest, List<Question> questionsWithSelectedAnswers, Long examineeId) {
        double fullPoints = 0.0;
        double grade = 0.0;
        // Loop through each question to calculate the score.
        for (Question question : questionsWithSelectedAnswers) {
            List<Answer> answers = question.getAnswers();
            // Check if the answers contain any mistakes:
            // A wrong answer is either a selected answer that is not correct
            // or a correct answer that was not selected.
            boolean containsWrongAnswers = answers.stream().anyMatch(
                    answer -> (answer.isSelected() && !answer.isCorrect()) || (answer.isCorrect() && !answer.isSelected()));
            // If there are no mistakes, add the question's points to the grade.
            if (!containsWrongAnswers)
                grade += question.getPoints();

            // Accumulate the total points available for the test.
            fullPoints += question.getPoints();
        }

        Long testId = takenTest.getId();
        String testName = takenTest.getName();
        double totalMark = takenTest.getTotalMark();
        // Determine if the user passed: here passing is defined as scoring at least 50% of the total points.
        boolean passed = grade >= (fullPoints * 0.5);

        // Create and return a Result object containing the test result details.
        Result result = new Result(testId, testName, grade, totalMark, passed, examineeId);
        return result;
    }

    /**
     * Handles POST requests to add a candidate (user) to a test.
     * This method assigns test access to the candidate, generates a verification code,
     * stores the code in the database, and sends an email with the code.
     *
     * @param userId             The ID of the user to add as a candidate.
     * @param testId             The ID of the test.
     * @param redirectAttributes Used to pass attributes during redirection.
     * @return                   A redirection to the questions page.
     */
    @RequestMapping(value = "/add-candidate", method = RequestMethod.POST)
    public String addCandidate(@RequestParam(value = "userId") Long userId, 
                               @RequestParam(value = "testId") Long testId,
                               RedirectAttributes redirectAttributes) {
        
        // Retrieve the candidate's user object from the database.
        Optional<User> userFromDb = userService.findById(userId);

        if (userFromDb.isPresent()) {
            // Grant test access to the candidate (1 indicates access granted).
            testService.setTestAccess(testId, userId, 1);

            // Generate a random 6-digit verification code.
            String verificationCode = generateVerificationCode();

            // Create a new VerificationCode object and populate it.
            VerificationCode verification = new VerificationCode();
            verification.setUserId(userId);
            verification.setTestId(testId);
            verification.setCode(verificationCode);
            // Optionally, set an expiry time for the verification code (commented out here).
            // verification.setExpiry(LocalDateTime.now().plusMinutes(10)); // Expires in 10 minutes
            // Save the verification code to the database.
            verificationCodeRepository.save(verification);

            // Send the verification code via email to the candidate.
            sendVerificationCode(userFromDb.get().getEmail(), verificationCode);
        }

        // Add the testId to the redirection attributes so that it remains available after redirection.
        redirectAttributes.addAttribute("testId", testId);
        // Redirect to the questions page where the candidate can proceed.
        return "redirect:/questions";
    }

    /**
     * Generates a random 6-digit verification code.
     *
     * @return A string containing a 6-digit code.
     */
    private String generateVerificationCode() {
        // Use Random to generate a number and format it as a 6-digit code.
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * Sends an email containing the verification code.
     *
     * @param email The recipient's email address.
     * @param code  The verification code to be sent.
     */
    private void sendVerificationCode(String email, String code) {
        // Create a SimpleMailMessage object.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Exam Verification Code");
        message.setText("Your verification code is: " + code);
        // Send the email using the MailSender.
        mailSender.send(message);
    }
}

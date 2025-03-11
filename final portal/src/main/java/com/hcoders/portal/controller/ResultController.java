package com.hcoders.portal.controller;

// Importing necessary Java classes for working with lists and dates.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Importing Spring Framework classes for dependency injection, security, and MVC.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// Importing application-specific classes for our domain model and services.
import com.hcoders.portal.model.FormView;
import com.hcoders.portal.model.Result;
import com.hcoders.portal.security.UserDetailsImpl;
import com.hcoders.portal.service.ResultService;

@Controller // Marks this class as a Spring MVC Controller to handle HTTP requests.
public class ResultController {

    // Injecting the ResultService bean with a specific implementation ("resultServiceImpl")
    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService;

    /**
     * Handles GET requests to the "/results" endpoint.
     * This method prepares the data needed to render the results view.
     *
     * @param userDetails The currently authenticated user's details.
     * @param model       The Model object used to pass data to the view.
     * @return            The name of the view to be rendered (e.g., "results").
     */
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public String viewTestsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        // Check if the current user has the role "ROLE_USER".
        // For normal users, we want to show only their specific results.
        if (userDetails.getUser().getRole().equals("ROLE_USER")) {
            // Retrieve results for the normal user using their ID.
            List<Result> resultsForNormalUser = resultService.findByExamineeId(userDetails.getId());
            // Count the number of results for the user.
            int numberForUserResults = resultsForNormalUser.size();
            // Add the count and list of results to the model to be used in the view.
            model.addAttribute("numberForUserResults", numberForUserResults);
            model.addAttribute("resultsForNormalUser", resultsForNormalUser);
        }

        // Retrieve a list of results for an admin view by converting raw query data
        // into a list of FormView objects. This method works even if the current user
        // is an admin, and it uses the admin's ID to filter results.
        List<FormView> resultsFormView = finResultsByAdminId(userDetails.getId());

        // Count the number of results prepared for admin view.
        int numberForAdminResults = resultsFormView.size();
        // Add the count and the list of FormView objects to the model.
        model.addAttribute("numberForAdminResults", numberForAdminResults);
        model.addAttribute("resultsFormView", resultsFormView);
        // Also add the current user details to the model.
        model.addAttribute("user", userDetails.getUser());

        // Return the name of the view template (e.g., "results") to render the results page.
        return "results";
    }

    /**
     * Retrieves and converts raw result data for a given admin ID into a list of FormView objects.
     *
     * @param adminId The ID of the admin whose results should be retrieved.
     * @return        A list of FormView objects representing the results.
     */
    public List<FormView> finResultsByAdminId(Long adminId) {
        // Retrieve raw results data as a list of Object arrays from the ResultService.
        List<Object[]> allResults = resultService.findAllByAdminId(adminId);

        // Create an empty list to store converted FormView objects.
        List<FormView> resultsFormView = new ArrayList<FormView>();
        // Iterate through each row of raw result data.
        for (int i = 0; i < allResults.size(); i++) {
            // Convert the current row into a FormView object.
            FormView formView = createFormViewForResult(allResults, i);
            // Add the created FormView object to the list.
            resultsFormView.add(formView);
        }
        // Return the list of FormView objects.
        return resultsFormView;
    }

    /**
     * Converts a specific row of raw result data into a FormView object.
     *
     * @param allResults The list of raw result data (each row is an Object array).
     * @param i          The index of the row to convert.
     * @return           A FormView object created from the raw data.
     */
    private FormView createFormViewForResult(List<Object[]> allResults, int i) {
        // Extract fields from the raw result data.
        String username = (String) allResults.get(i)[0];
        String testName = (String) allResults.get(i)[1];
        Date date = (Date) allResults.get(i)[2];
        Double totalMark = (Double) allResults.get(i)[3];
        Double grade = (Double) allResults.get(i)[4];
        Boolean passed = (Boolean) allResults.get(i)[5];
        // Create a new FormView object using the extracted data.
        FormView formView = new FormView(username, testName, date, totalMark, grade, passed);
        // Return the constructed FormView object.
        return formView;
    }
}

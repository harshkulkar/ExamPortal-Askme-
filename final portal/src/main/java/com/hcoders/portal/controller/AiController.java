package com.hcoders.portal.controller;

// Import required Spring MVC and dependency injection classes.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Import the AiService which contains the logic for interacting with the AI.
import com.hcoders.portal.service.AiService;

@Controller // Marks this class as a Spring MVC controller.
@RequestMapping("/ai-response") // Maps all handler methods in this class to URLs starting with /ai-response.
public class AiController {

    // The AiService is injected to handle the AI-related logic.
    @Autowired
    private AiService aiService;

    // Constructor-based dependency injection for AiService.
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * Handles GET requests to /ai-response.
     * Retrieves the "query" parameter from the URL (or uses a default value),
     * then gets an AI-generated response and adds both to the model.
     *
     * @param query the user-provided query string; defaults to "I need help" if not provided.
     * @param model the Spring MVC Model used to pass attributes to the view.
     * @return the name of the view to be rendered (ai-response.html).
     */
    @GetMapping
    public String askAi(
            @RequestParam(value = "query", required = false, defaultValue = "I need help") String query,
            Model model) {

        // Call the aiService to get a response based on the query.
        String response = aiService.askAi(query);

        // Add the original query and the AI response to the model, so they can be accessed in the view.
        model.addAttribute("query", query);
        model.addAttribute("response", response);

        // Return the name of the view (ai-response.html in the templates folder) to be rendered.
        return "ai-response";
    }
}

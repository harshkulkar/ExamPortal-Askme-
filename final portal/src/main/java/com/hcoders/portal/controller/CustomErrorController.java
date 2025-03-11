package com.hcoders.portal.controller;

// Import Spring Boot's ErrorController interface to handle errors
import org.springframework.boot.web.servlet.error.ErrorController;
// Import HttpStatus to compare error status codes with standard HTTP status values
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Import Jakarta Servlet classes to work with HTTP request attributes
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller // Marks this class as a Spring MVC Controller
public class CustomErrorController implements ErrorController {

    /**
     * Handles requests to the "/error" URL.
     * Determines which error page to display based on the HTTP status code.
     *
     * @param request The HttpServletRequest that contains attributes about the error.
     * @return A String representing the view name for the appropriate error page.
     */
    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Set a default error page (HTTP 404 Not Found)
        String errorPage = "error/404";

        // Retrieve the error status code from the request attributes.
        // RequestDispatcher.ERROR_STATUS_CODE contains the HTTP status code of the error.
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // If the error status code is present, determine the proper error view to display.
        if (status != null) {
            // Convert the status code from an Object to an Integer.
            Integer statusCode = Integer.valueOf(status.toString());

            // Check if the status code is 404 (Not Found)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // For a 404 error, use the "error/404" view.
                errorPage = "error/404";
            } 
            // Check if the status code is 405 (Method Not Allowed)
            else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                // For a 405 error, use the "error/405" view.
                errorPage = "error/405";
            } 
            // Check if the status code is 403 (Forbidden)
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // For a 403 error, use the "error/403" view.
                errorPage = "error/403";
            } 
            // Check if the status code is 500 (Internal Server Error)
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // For a 500 error, use the "error/500" view.
                errorPage = "error/500";
            }
        }

        // Return the name of the error view to be rendered.
        return errorPage;
    }
}

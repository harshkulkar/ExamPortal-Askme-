package com.hcoders.portal.controller;



import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

	@GetMapping("/error")
	public String handleError(HttpServletRequest request) {
		String errorPage = "error/404"; // default

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				// handle HTTP 404 Not Found error
				errorPage = "error/404";

			} else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
				// handle HTTP 405 Forbidden error
				errorPage = "error/405";

			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				// handle HTTP 403 Forbidden error
				errorPage = "error/403";

			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				// handle HTTP 500 Internal Server error
				errorPage = "error/500";

			}
		}

		return errorPage;
	}


}
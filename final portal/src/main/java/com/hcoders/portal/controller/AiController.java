package com.hcoders.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hcoders.portal.service.AiService;

@Controller
@RequestMapping("/ai-response")
public class AiController {

    @Autowired
    private AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping
    public String askAi(
            @RequestParam(value = "query", required = false, defaultValue = "How are you?") String query,
            Model model) {

        String response = aiService.askAi(query);
        model.addAttribute("query", query);
        model.addAttribute("response", response);
        return "ai-response"; // This refers to ai-response.html in the templates folder
    }
}

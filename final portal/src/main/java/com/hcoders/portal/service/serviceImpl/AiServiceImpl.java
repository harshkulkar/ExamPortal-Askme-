package com.hcoders.portal.service.serviceImpl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.hcoders.portal.service.AiService;

@Service // Marks this class as a Spring Service component.
public class AiServiceImpl implements AiService {

    // ChatClient is used to interact with the AI chat system.
    private ChatClient chatClient;
    
    /**
     * Constructor for AiServiceImpl.
     * It uses the provided ChatClient.Builder to build and initialize the ChatClient.
     *
     * @param builder the ChatClient.Builder used to configure and build the ChatClient.
     */
    public AiServiceImpl(ChatClient.Builder builder) {
        // Build the ChatClient instance using the provided builder.
        this.chatClient = builder.build();
    }   
    
    /**
     * Sends a query to the AI and retrieves the response.
     * 
     * This method uses the ChatClient to send the prompt, waits for the call to complete,
     * and then extracts the content of the response.
     *
     * @param question the query to be sent to the AI system.
     * @return a String containing the AI's response.
     */
    @Override
    public String askAi(String question) {
        // Send the question to the AI chat system, call the endpoint, and return the content of the response.
        return chatClient.prompt(question).call().content();
    }
}

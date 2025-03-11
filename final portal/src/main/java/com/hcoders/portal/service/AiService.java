package com.hcoders.portal.service;

/**
 * AiService defines the contract for interacting with the AI system.
 * 
 * Implementations of this service should provide the functionality to send a query to the AI and receive a response.
 */
public interface AiService {
    
    /**
     * Sends a query to the AI and returns the response.
     *
     * @param question the query to be sent to the AI system.
     * @return a String containing the AI's response.
     */
    String askAi(String question); 
}

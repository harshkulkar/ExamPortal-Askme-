package com.hcoders.portal.service.serviceImpl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.hcoders.portal.service.AiService;


@Service
public class AiServiceImpl implements AiService{

	private ChatClient chatClient;
	
	public AiServiceImpl (ChatClient.Builder builder)
	{
		
		this.chatClient = builder.build();
		
	}	
	
	@Override
	public String askAi(String Question) {
		
		return chatClient.prompt(Question).call().content();
	}

	
		
	
}

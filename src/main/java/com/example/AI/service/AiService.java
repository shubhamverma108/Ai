package com.example.AI.service;


import com.example.AI.dto.AiResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public AiResponse aiResponse(String message){
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(AiResponse.class);
    }


    //list form response
    public List<AiResponse> aiResponseInListForm(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<AiResponse>>() {});
    }
}

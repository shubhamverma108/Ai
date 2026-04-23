package com.example.AI.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

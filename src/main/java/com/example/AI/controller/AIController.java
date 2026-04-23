package com.example.AI.controller;


import com.example.AI.dto.AiResponse;
import com.example.AI.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    @Autowired
    private AiService aiService;

    @GetMapping("/ai/chat")
    public AiResponse chat(@RequestParam String message) {
        return aiService.aiResponse(message);
    }
}

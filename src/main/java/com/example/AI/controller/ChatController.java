package com.example.AI.controller;

import com.example.AI.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private RagService ragService;

//    @GetMapping("/ask")
//    public String ask(@RequestParam String question) {
//        return ragService.askQuestion(question);
//    }
}

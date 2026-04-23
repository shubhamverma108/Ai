package com.example.AI.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "wa_chat_history")
@Data
public class WhatsAppChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String waId;   // phone number

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String botReply;

    private LocalDateTime createdAt = LocalDateTime.now();

}

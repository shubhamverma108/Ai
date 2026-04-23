package com.example.AI;

import com.example.AI.entity.WhatsAppChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhatsAppChatHistoryRepo extends JpaRepository<WhatsAppChatHistory, Long> {

    List<WhatsAppChatHistory> findTop10ByWaIdOrderByCreatedAtDesc(String waId);
}
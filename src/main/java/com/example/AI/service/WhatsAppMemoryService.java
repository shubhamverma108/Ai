package com.example.AI.service;

import com.example.AI.WhatsAppChatHistoryRepo;
import com.example.AI.entity.WhatsAppChatHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsAppMemoryService {

    private final WhatsAppChatHistoryRepo repo;

    public WhatsAppMemoryService(WhatsAppChatHistoryRepo repo) {
        this.repo = repo;
    }

    public void saveChat(String waId, String userMsg, String botMsg) {
        WhatsAppChatHistory history = new WhatsAppChatHistory();
        history.setWaId(waId);
        history.setUserMessage(userMsg);
        history.setBotReply(botMsg);
        repo.save(history);
    }

    public List<WhatsAppChatHistory> getLastChats(String waId) {
        return repo.findTop10ByWaIdOrderByCreatedAtDesc(waId);
    }
}

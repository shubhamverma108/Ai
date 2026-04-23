package com.example.AI.service;

import com.example.AI.dto.WhatsAppMessageDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, WhatsAppMessageDTO> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, WhatsAppMessageDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(WhatsAppMessageDTO dto) {
        kafkaTemplate.send("whatsapp-topic", dto);
    }
}

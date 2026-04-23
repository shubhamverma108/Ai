package com.example.AI.controller;

import com.example.AI.dto.WhatsAppMessageDTO;
import com.example.AI.service.AiService;
import com.example.AI.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    @Autowired
    private AiService aiService;
    private final RestTemplate restTemplate;
    private static final String VERIFY_TOKEN = "my_verify_token";

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public WhatsAppWebhookController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return challenge;
        }
        return "error";
    }


    // only for test and pdf upload locally via a api
    /*@PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody String payload) {

        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(payload);

                JsonNode messagesNode = root.path("entry").get(0)
                        .path("changes").get(0)
                        .path("value")
                        .path("messages");

                if (!messagesNode.isArray() || messagesNode.size() == 0) return;

                String fromNumber = messagesNode.get(0).path("from").asText();
                String messageText = messagesNode.get(0).path("text").path("body").asText();

               *//* String aiReply = aiService.aiResponse(messageText).getContent();
                sendMessage(fromNumber, aiReply);*//*

                WhatsAppMessageDTO dto = new WhatsAppMessageDTO();
                dto.setFrom(fromNumber);
                dto.setText(messageText);

                kafkaProducerService.sendMessage(dto);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok("EVENT_RECEIVED");
    }*/

    @PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody String payload) {

        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(payload);

                JsonNode messagesNode = root.path("entry").get(0)
                        .path("changes").get(0)
                        .path("value")
                        .path("messages");

                if (!messagesNode.isArray() || messagesNode.size() == 0) return;

                JsonNode messageNode = messagesNode.get(0);

                String fromNumber = messageNode.path("from").asText();
                String type = messageNode.path("type").asText();

                WhatsAppMessageDTO dto = new WhatsAppMessageDTO();
                dto.setFrom(fromNumber);
                dto.setType(type);

                // ✅ If Text Message
                if ("text".equalsIgnoreCase(type)) {
                    String messageText = messageNode.path("text").path("body").asText();
                    dto.setText(messageText);

                    kafkaProducerService.sendMessage(dto);
                }

                // ✅ If Document Message (PDF)
                else if ("document".equalsIgnoreCase(type)) {
                    String mediaId = messageNode.path("document").path("id").asText();
                    String fileName = messageNode.path("document").path("filename").asText();
                    String mimeType = messageNode.path("document").path("mime_type").asText();

                    dto.setMediaId(mediaId);
                    dto.setFileName(fileName);
                    dto.setMimeType(mimeType);

                    kafkaProducerService.sendMessage(dto);
                }

                // ❌ Unsupported type
                else {
                    System.out.println("Unsupported message type: " + type);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

}

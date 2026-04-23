package com.example.AI.service;

import com.example.AI.dto.WhatsAppMessageDTO;
import com.example.AI.entity.WhatsAppChatHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;



@Service
public class KafkaConsumerService {

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${whatsapp.access-token}")
    private String accessToken;
    @Autowired
    private AiService aiService;

    @Autowired
    private RagService ragGeminiService;

    @Autowired
    private WhatsAppMediaDownloadService mediaDownloadService;
    @Autowired
    private PdfService pdfService;

    private final RestTemplate restTemplate;

    public KafkaConsumerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Autowired
    private WhatsAppMemoryService memoryService;

    /*@KafkaListener(topics = "whatsapp-topic", groupId = "whatsapp-group")
    public void consume(WhatsAppMessageDTO dto) {

        try {
            System.out.println("Received from Kafka: " + dto.getText());

            // 1. Fetch last chats for this user
            List<WhatsAppChatHistory> historyList =
                    memoryService.getLastChats(dto.getFrom());

            // 2. Build prompt with memory
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a helpful WhatsApp assistant.\n\n");

            for (int i = historyList.size() - 1; i >= 0; i--) {
                WhatsAppChatHistory h = historyList.get(i);
                prompt.append("User: ").append(h.getUserMessage()).append("\n");
                prompt.append("Assistant: ").append(h.getBotReply()).append("\n");
            }

            prompt.append("User: ").append(dto.getText()).append("\n");
            prompt.append("Assistant: ");

            // 3. Get AI response
            // ollama api
//          String reply = aiService.aiResponse(prompt.toString()).getContent();

            String reply = ragGeminiService.askQuestionUsingPdf(prompt.toString());
            // 4. Save conversation to DB
            memoryService.saveChat(dto.getFrom(), dto.getText(), reply);

            // 5. Send reply
            sendMessage(dto.getFrom(), reply);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    @KafkaListener(topics = "whatsapp-topic", groupId = "whatsapp-group")
    public void consume(WhatsAppMessageDTO dto) {

        try {

            System.out.println("Received from Kafka: type=" + dto.getType());

            // ✅ CASE 1: Document Upload (PDF)
            if ("document".equalsIgnoreCase(dto.getType())) {

                // 1. Download PDF from Meta Server using mediaId
                String filePath = mediaDownloadService.downloadAndSavePdf(
                        dto.getMediaId(),
                        dto.getFileName()
                );

                System.out.println("PDF downloaded and saved at: " + filePath);

                // 2. Extract text + chunking + store into Vector DB
                pdfService.processPdf(filePath, dto.getFrom());

                // 3. Send success message to WhatsApp
                sendMessage(dto.getFrom(), "✅ PDF uploaded successfully. Now ask your question.");

                return;
            }

            // ✅ CASE 2: Text Question
            if ("text".equalsIgnoreCase(dto.getType())) {

                // 1. Fetch last chats for this user
                List<WhatsAppChatHistory> historyList =
                        memoryService.getLastChats(dto.getFrom());

                // 2. Build prompt with memory
                StringBuilder prompt = new StringBuilder();
                prompt.append("You are a helpful WhatsApp assistant.\n\n");

                for (int i = historyList.size() - 1; i >= 0; i--) {
                    WhatsAppChatHistory h = historyList.get(i);
                    prompt.append("User: ").append(h.getUserMessage()).append("\n");
                    prompt.append("Assistant: ").append(h.getBotReply()).append("\n");
                }

                prompt.append("User: ").append(dto.getText()).append("\n");
                prompt.append("Assistant: ");

                // 3. Ask Question using Vector DB + Gemini
                String reply = ragGeminiService.askQuestionUsingPdf(dto.getFrom(), prompt.toString());

                // 4. Save conversation to DB
                memoryService.saveChat(dto.getFrom(), dto.getText(), reply);

                // 5. Send reply
                sendMessage(dto.getFrom(), reply);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(dto.getFrom(), "❌ Something went wrong. Please try again.");
        }
    }
    public void sendMessage(String to, String message) {

        String phoneNumberId = "965348293339331";
//        String accessToken = "EAF5SG16NsNsBRHEpZAouVGziOz09ebiuZAeZASQZBQrr1HLdIsFX0NHeZBUoLernTyLuSrMVJdOlzm7MoRULOvy36GRzTnskPRNZAtF76HE7MQCHUwkfanWfoviGltvr7Vuq69OHk1erCaAeW6MkatlVK1xjwE8RPVSy7Owe0IRfIBvjVV2G2IxfDw6RXn5ZCgPYndZC4oR1HgO80I5dAYGAaQiZBI1i68gTtToVRFor22aN9KD8lZAk5ax4NlfONyn6W10YZCGkgArE9fggSkVsVRyr8rdEVaJbhzH5YWLaQZDZD";

        String url = "https://graph.facebook.com/v18.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var body = new java.util.HashMap<String, Object>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        body.put("type", "text");

        var text = new java.util.HashMap<String, String>();
        text.put("body", message);

        body.put("text", text);

        HttpEntity<Object> request = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(url, request, String.class);
        System.out.println("WhatsApp Response: " + response);
    }




}

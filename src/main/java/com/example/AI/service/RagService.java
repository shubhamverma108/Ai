package com.example.AI.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    private final RestTemplate restTemplate;

    @Value("${gemini.api-key}")
    private String geminiApiKey;


    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder,RestTemplate restTemplate) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        this.restTemplate = restTemplate;
    }

    //this is for internal llm model
/*    public String askQuestion(String question) {

        List<Document> documents = vectorStore.similaritySearch(question);

        String context = documents.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        return chatClient.prompt()
                .system("Answer the question using the provided context")
                .user("Context:\n" + context + "\n\nQuestion:" + question)
                .call()
                .content();
    }*/

   /* public String askQuestionUsingPdf(String question) {

        // 1. Fetch Similar Docs from Vector DB
        List<Document> documents = vectorStore.similaritySearch(question);

        String context = documents.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        // 2. Create Gemini Prompt with Context
        String finalPrompt = """
                You are a helpful assistant.
                Answer only using the below context (PDF content).
                If answer is not found in context, say: "Sorry, not available in the PDF."

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        // 3. Call Gemini
        return callGemini(finalPrompt);
    }*/

    public String askQuestionUsingPdf(String fromNumber, String question) {

        // 1. similarity search using SearchRequest
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build()
        );

        // 2. Filter documents for current user
        documents = documents.stream()
                .filter(doc -> fromNumber.equals(doc.getMetadata().get("user")))
                .toList();


        if (documents == null || documents.isEmpty()) {
            return "❌ No PDF found for your account. Please upload a PDF first.";
        }

        String context = documents.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        // 2. Create Gemini Prompt with Context
        String finalPrompt = """
            You are a helpful assistant.
            Answer only using the below context (PDF content).
            If answer is not found in context, say: "Sorry, not available in the PDF."

            Context:
            %s

            Question:
            %s
            """.formatted(context, question);

        // 3. Call Gemini
        return callGemini(finalPrompt);
    }

    public String callGemini(String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        java.util.Map<String, Object> textPart = java.util.Map.of("text", prompt);
        java.util.Map<String, Object> parts = java.util.Map.of("parts", List.of(textPart));
        java.util.Map<String, Object> requestBody = java.util.Map.of("contents", List.of(parts));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return extractGeminiText(response.getBody());
    }

    public String extractGeminiText(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}
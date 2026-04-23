package com.example.AI.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class WhatsAppMediaDownloadService {

    @Value("${whatsapp.access-token}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public WhatsAppMediaDownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ Step 1: Get download URL using mediaId
    public String getMediaUrl(String mediaId) {

        String url = "https://graph.facebook.com/v18.0/" + mediaId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            return root.path("url").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse media URL response", e);
        }
    }

    // ✅ Step 2: Download PDF bytes from download URL
    public byte[] downloadMedia(String mediaUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response =
                restTemplate.exchange(mediaUrl, HttpMethod.GET, request, byte[].class);

        return response.getBody();
    }

    // ✅ Step 3: Save PDF locally
    public String savePdfToLocal(byte[] pdfBytes, String fileName) {

        try {
            File folder = new File("uploaded_pdfs");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = "uploaded_pdfs/" + fileName;

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(pdfBytes);
            fos.close();

            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save PDF locally", e);
        }
    }

    // ✅ Final method: mediaId -> download -> save
    public String downloadAndSavePdf(String mediaId, String fileName) {

        // 1. Get secure download URL from Meta Graph API
        String mediaUrl = getMediaUrl(mediaId);

        // 2. Download bytes
        byte[] pdfBytes = downloadMedia(mediaUrl);

        // 3. Save locally
        return savePdfToLocal(pdfBytes, fileName);
    }
}

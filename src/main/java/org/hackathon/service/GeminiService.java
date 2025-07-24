package org.hackathon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    public String getReplyFromGemini(String message) {
        System.out.println("API key" + apiKey);
        String apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-pro:generateContent?key=" + apiKey;

        // Request body for Gemini
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        parts.add(Collections.singletonMap("text", message));
        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);
        requestBody.put("contents", Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode replyNode = root.path("candidates").get(0)
                        .path("content").path("parts").get(0).path("text");
                return replyNode.asText("Sorry, I couldn’t understand that.");
            } else {
                return "Gemini API error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, something went wrong.";
        }
    }

    public String getReplyWithContext(List<String> contextMessages, String latestMessage) {
        StringBuilder fullPrompt = new StringBuilder();

        for (String line : contextMessages) {
            fullPrompt.append("User: ").append(line).append("\n");
        }
        fullPrompt.append("User: ").append(latestMessage);

        return getReplyFromGemini(fullPrompt.toString());
    }


}
package com.taskmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.model.Task;

@Service
public class AIService {

	@Value("${gemini.api-key}")
    private String apiKey;

    public Task generateTaskFromText(String inputText) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("""
        		Extract the task details from this sentence:
        		"%s"

        		- Interpret relative dates like "next Monday" or "in two days" as exact calendar dates.
        		- Use current date: %s
        		- Respond ONLY in this exact JSON format:
        		{
        		  "title": "...",
        		  "description": "...",
        		  "deadline": "YYYY-MM-DD",
        		  "status": "PENDING"
        		}
        		""", inputText, LocalDate.now());


        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("candidates")) {
            throw new RuntimeException("Gemini response missing 'candidates'");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");

        Map<String, Object> firstCandidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");

        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        Map<String, Object> firstPart = parts.get(0);
        String text = (String) firstPart.get("text");

        // Parse JSON from Gemini output
        ObjectMapper mapper = new ObjectMapper();
        int index = text.indexOf('{'); // skip anything before JSON
        Task task = null;
        try {
            task = mapper.readValue(text.substring(index), Task.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }

        return task;
    }
}

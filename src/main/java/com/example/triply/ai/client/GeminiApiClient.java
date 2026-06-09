package com.example.triply.ai.client;

import com.example.triply.ai.exception.GeminiApiException;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeminiApiClient {

    private final Client client;
    private final String modelName;

    public GeminiApiClient(Client geminiClient,
                           @Qualifier("geminiModelName") String geminiModelName) {
        this.client = geminiClient;
        this.modelName = geminiModelName;
    }

    public String generateContent(String prompt) {
        try {
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .build();

            GenerateContentResponse response = client.models.generateContent(modelName, prompt, config);
            String text = response.text();

            if (text == null || text.isBlank()) {
                throw new GeminiApiException("Gemini returned an empty response");
            }

            log.debug("Gemini response received, length={}", text.length());
            return text;

        } catch (GeminiApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage(), e);
            throw new GeminiApiException("Gemini API call failed: " + e.getMessage(), e);

        }
    }
}

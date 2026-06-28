package com.chatbot.service;

import com.chatbot.model.OpenAIRequest;
import com.chatbot.model.OpenAIRequest.OpenAIMessage;
import com.chatbot.model.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Service responsible for calling the OpenAI Chat Completions API.
 *
 * Flow:
 *  1. Build request with system prompt + user message
 *  2. Call OpenAI API using RestTemplate
 *  3. Extract and return the reply text
 */
@Service
public class OpenAIService {

    // Injected from application.properties
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max.tokens}")
    private int maxTokens;

    private final PromptBuilderService promptBuilderService;

    // RestTemplate is used to make HTTP calls to OpenAI
    private final RestTemplate restTemplate = new RestTemplate();

    // Constructor injection (best practice)
    public OpenAIService(PromptBuilderService promptBuilderService) {
        this.promptBuilderService = promptBuilderService;
    }

    /**
     * Sends user message to OpenAI and returns the AI reply.
     *
     * @param userMessage  - what the user typed
     * @param topic        - college / restaurant / product / hospital
     * @return             - AI generated reply string
     */
    public String getAIReply(String userMessage, String topic) {

        // Step 1: Build the system prompt for the given topic
        String systemPrompt = promptBuilderService.buildSystemPrompt(topic);

        // Step 2: Create messages list (system + user)
        // "system" message = instructions for the AI
        // "user" message   = what the user asked
        List<OpenAIMessage> messages = List.of(
                new OpenAIMessage("system", systemPrompt),
                new OpenAIMessage("user", userMessage)
        );

        // Step 3: Create the full request body
        OpenAIRequest requestBody = new OpenAIRequest(
                model,      // e.g. "gpt-3.5-turbo"
                messages,
                maxTokens,  // max words in response
                0.7         // temperature: 0=strict, 1=creative
        );

        // Step 4: Set HTTP headers (Authorization + Content-Type)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // Adds "Authorization: Bearer sk-..."

        // Step 5: Wrap body + headers into HttpEntity
        HttpEntity<OpenAIRequest> httpEntity = new HttpEntity<>(requestBody, headers);

        // Step 6: Make the POST call to OpenAI
        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                httpEntity,
                OpenAIResponse.class  // auto-maps JSON response to this class
        );

        // Step 7: Extract and return the reply text
        if (response.getBody() != null) {
            return response.getBody().getReplyText();
        }

        return "Sorry, I couldn't get a response. Please try again.";
    }
}

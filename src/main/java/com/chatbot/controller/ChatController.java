package com.chatbot.controller;

import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller that exposes chatbot API endpoints.
 *
 * Endpoints:
 *  POST /api/chat        → Send message, get AI reply
 *  GET  /api/chat/info   → Get chatbot configuration info
 *  GET  /api/chat/health → Health check
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${chatbot.topic}")
    private String defaultTopic;

    @Value("${chatbot.name}")
    private String chatbotName;

    @Value("${chatbot.welcome.message}")
    private String welcomeMessage;

    private final OpenAIService openAIService;

    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    // =============================================
    //  POST /api/chat
    //  Main endpoint: user sends message, gets reply
    // =============================================
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {

        // Validate: message should not be empty
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.error("Message cannot be empty!"));
        }

        // Use topic from request, or fall back to default from properties
        String topic = (request.getTopic() != null && !request.getTopic().isEmpty())
                ? request.getTopic()
                : defaultTopic;

        try {
            // Call OpenAI Service to get the AI reply
            String aiReply = openAIService.getAIReply(request.getMessage(), topic);

            // Return success response
            return ResponseEntity.ok(ChatResponse.success(aiReply));

        } catch (Exception e) {
            // Log the error and return a friendly message
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.error("Oops! Something went wrong. Please try again."));
        }
    }

    // =============================================
    //  GET /api/chat/info
    //  Returns chatbot config (used by frontend on load)
    // =============================================
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getChatbotInfo() {
        return ResponseEntity.ok(Map.of(
                "name", chatbotName,
                "topic", defaultTopic,
                "welcomeMessage", welcomeMessage
        ));
    }

    // =============================================
    //  GET /api/chat/health
    //  Simple health check endpoint
    // =============================================
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "FAQ Chatbot is running!"
        ));
    }
}

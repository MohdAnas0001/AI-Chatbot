package com.chatbot.model;

import java.util.List;

// =============================================
//  These classes map to OpenAI API JSON format
// =============================================

// --- Request Body sent to OpenAI ---
public class OpenAIRequest {

    private String model;
    private List<OpenAIMessage> messages;
    private int max_tokens;
    private double temperature;

    public OpenAIRequest() {}

    public OpenAIRequest(String model, List<OpenAIMessage> messages, int max_tokens, double temperature) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<OpenAIMessage> getMessages() { return messages; }
    public void setMessages(List<OpenAIMessage> messages) { this.messages = messages; }

    public int getMax_tokens() { return max_tokens; }
    public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }


    // --- Inner class: A single message in the conversation ---
    public static class OpenAIMessage {
        private String role;    // "system", "user", or "assistant"
        private String content;

        public OpenAIMessage() {}

        public OpenAIMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}

package com.chatbot.model;

// Request from frontend → backend
public class ChatRequest {

    private String message;
    private String topic;  // 

    
    public ChatRequest() {}

    public ChatRequest(String message, String topic) {
        this.message = message;
        this.topic = topic;
    }

    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}

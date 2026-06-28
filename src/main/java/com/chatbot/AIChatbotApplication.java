package com.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AIChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIChatbotApplication.class, args);
        System.out.println("====================================");
        System.out.println("  FAQ Chatbot is running!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("====================================");
        
    }
}

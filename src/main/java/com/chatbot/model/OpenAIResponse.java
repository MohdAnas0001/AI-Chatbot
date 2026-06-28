package com.chatbot.model;

import java.util.List;

// Maps the JSON response received FROM OpenAI API
public class OpenAIResponse {

    private String id;
    private List<Choice> choices;
    private Usage usage;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }

    // Helper method to directly get the reply text
    public String getReplyText() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent();
        }
        return "Sorry, I couldn't generate a response.";
    }

    // --- Inner class: One choice/response from OpenAI ---
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }

        public String getFinish_reason() { return finish_reason; }
        public void setFinish_reason(String finish_reason) { this.finish_reason = finish_reason; }
    }

    // --- Inner class: Message inside a choice ---
    public static class Message {
        private String role;
        private String content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // --- Inner class: Token usage info ---
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        public int getPrompt_tokens() { return prompt_tokens; }
        public void setPrompt_tokens(int prompt_tokens) { this.prompt_tokens = prompt_tokens; }

        public int getCompletion_tokens() { return completion_tokens; }
        public void setCompletion_tokens(int completion_tokens) { this.completion_tokens = completion_tokens; }

        public int getTotal_tokens() { return total_tokens; }
        public void setTotal_tokens(int total_tokens) { this.total_tokens = total_tokens; }
    }
}

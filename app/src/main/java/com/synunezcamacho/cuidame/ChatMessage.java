package com.synunezcamacho.cuidame;

public class ChatMessage {
    public String content;
    public String senderId;
    public String username;
    public String insertedAt;

    public ChatMessage(String senderId, String content, String username, String insertedAt) {
        this.senderId = senderId;
        this.content = content;
        this.username = username;
        this.insertedAt = insertedAt;
    }
}


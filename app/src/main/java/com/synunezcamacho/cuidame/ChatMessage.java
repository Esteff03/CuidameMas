package com.synunezcamacho.cuidame;

public class ChatMessage {
    public String content;
    public String username;
    public String insertedAt;

    public ChatMessage(String content, String username, String insertedAt) {
        this.content = content;
        this.username = username;
        this.insertedAt = insertedAt;
    }
}


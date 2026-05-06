package com.example.recyclemarketplace.model;

public class ChatModel {
    public String name;
    public String message;
    public String time;
    public int badge;

    public ChatModel() {
        // kosong (dibutuhkan Firebase kadang)
    }

    public ChatModel(String name, String message, String time, int badge) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.badge = badge;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int getBadge() {
        return badge;
    }
}
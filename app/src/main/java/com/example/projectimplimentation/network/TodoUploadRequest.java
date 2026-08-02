package com.example.projectimplimentation.network;

public class TodoUploadRequest {
    private String title;
    private boolean completed;
    private int userId;

    public TodoUploadRequest(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
        this.userId = 1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

package com.example.Bhoklagyo.dto;

public class FeedbackRequest {
    private String feedback;

    public FeedbackRequest() {}

    public FeedbackRequest(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

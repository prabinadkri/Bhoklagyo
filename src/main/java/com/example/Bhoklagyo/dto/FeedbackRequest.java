package com.example.Bhoklagyo.dto;

public class FeedbackRequest {
    private String feedback;
    private Integer rating;

    public FeedbackRequest() {}

    public FeedbackRequest(String feedback) {
        this.feedback = feedback;
    }

    public FeedbackRequest(String feedback, Integer rating) {
        this.feedback = feedback;
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}

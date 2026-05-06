package com.example.recyclemarketplace.model;

public class ReviewModel {

    private String productName;
    private String imageUrl;
    private String reviewText;
    private int rating;
    private long timestamp;

    // 🔥 WAJIB untuk Firebase
    public ReviewModel() {
        this.productName = "";
        this.imageUrl = "";
        this.reviewText = "";
        this.rating = 0;
        this.timestamp = 0;
    }

    // 🔥 CONSTRUCTOR
    public ReviewModel(String productName, String imageUrl,
                       String reviewText, int rating, long timestamp) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.reviewText = reviewText;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    // ================= GETTER =================
    public String getProductName() {
        return productName != null ? productName : "-";
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public String getReviewText() {
        return reviewText != null ? reviewText : "-";
    }

    public int getRating() {
        return rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // ================= SETTER =================
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // 🔥 OPTIONAL: bantu cek review valid
    public boolean isValid() {
        return productName != null && !productName.isEmpty()
                && rating > 0;
    }
}
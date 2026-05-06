package com.example.recyclemarketplace.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ProductModel {

    // 🔥 SESUAI FIREBASE
    private String id;
    private String name;
    private String desc;
    private int price;
    private String image;
    private String category;
    private int stock;

    // 🔥 TAMBAHAN (APP)
    private String sellerId;
    private double rating;
    private int reviewCount;

    // 🔥 OPTIONAL (LOCAL DRAWABLE)
    private int imageResId;

    // 🔥 WAJIB UNTUK FIREBASE
    public ProductModel() {
    }

    // ✅ CONSTRUCTOR FIREBASE (Cloudinary URL)
    public ProductModel(String id, String name, String desc,
                        int price, String image, String category,
                        int stock, String sellerId,
                        double rating, int reviewCount) {

        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stock = stock;
        this.sellerId = sellerId;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    // ✅ CONSTRUCTOR LOCAL (DRAWABLE)
    public ProductModel(String id, String name, String desc,
                        int price, int imageResId,
                        double rating, int reviewCount) {

        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageResId = imageResId;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    // =========================
    // 🔥 GETTER (FIREBASE)
    // =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public String getSellerId() {
        return sellerId;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    // =========================
    // 🔥 BACKWARD COMPATIBILITY
    // (BIAR KODE LAMA TIDAK ERROR)
    // =========================

    public String getNama() {
        return name;
    }

    public String getDeskripsi() {
        return desc;
    }

    public int getHarga() {
        return price;
    }

    public String getImgUrl() {
        return image;
    }
}
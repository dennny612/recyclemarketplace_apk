package com.example.recyclemarketplace.model;

public class MarketProductModel {

    private String id;
    private String name;
    private int price;
    private String category;
    private String image;   // 🔥 URL dari Cloudinary / Firebase
    private String desc;

    private int imageResId; // 🔥 untuk drawable (local)

    // 🔥 WAJIB untuk Firebase
    public MarketProductModel() {
    }

    // ✅ CONSTRUCTOR UNTUK FIREBASE (URL)
    public MarketProductModel(String id, String name, int price,
                              String category, String image, String desc) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.desc = desc;
    }

    // ✅ CONSTRUCTOR UNTUK LOCAL (DRAWABLE)
    public MarketProductModel(String name, int price,
                              String category, int imageResId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
    }

    // 🔥 GETTER (WAJIB SESUAI YANG DIPAKAI DI ACTIVITY)

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }

    public int getImageResId() {
        return imageResId;
    }
}
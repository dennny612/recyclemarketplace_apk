package com.example.recyclemarketplace.model;

public class CartModel {

    private String id;
    private String nama;
    private int harga;
    private int qty;
    private String imgUrl;
    private boolean checked;

    // 🔥 WAJIB untuk Firebase
    public CartModel() {
    }

    public CartModel(String id, String nama, int harga, int qty,
                     String imgUrl, boolean checked) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.qty = qty;
        this.imgUrl = imgUrl;
        this.checked = checked;
    }

    // 🔥 GETTER
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getHarga() {
        return harga;
    }

    public int getQty() {
        return qty;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public boolean isChecked() {
        return checked;
    }

    // 🔥 SETTER (WAJIB LENGKAP)
    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    // 🔥 TOTAL HARGA
    public int getTotalPrice() {
        return harga * qty;
    }
}
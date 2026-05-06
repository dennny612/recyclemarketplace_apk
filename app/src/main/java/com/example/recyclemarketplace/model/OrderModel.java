package com.example.recyclemarketplace.model;

public class OrderModel {

    private String orderId;
    private String userId;
    private String items;

    private int subtotal;
    private int ongkir;
    private int total;

    private String status;
    private long timestamp;

    // 🔥 DATA PENGIRIMAN
    private String nama;
    private String phone;
    private String address;

    // 🔥 PEMBAYARAN & PENGIRIMAN
    private String payment;
    private String courier;
    private int admin;

    // 🔥 WAJIB (Firebase butuh constructor kosong)
    public OrderModel() {}

    // 🔥 CONSTRUCTOR FINAL
    public OrderModel(String orderId, String userId, String items,
                      int subtotal, int ongkir, int total,
                      String status, long timestamp,
                      String nama, String phone, String address,
                      String payment, String courier, int admin) {

        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.subtotal = subtotal;
        this.ongkir = ongkir;
        this.total = total;
        this.status = status;
        this.timestamp = timestamp;
        this.nama = nama;
        this.phone = phone;
        this.address = address;
        this.payment = payment;
        this.courier = courier;
        this.admin = admin;
    }

    // ================= GETTER =================

    public String getOrderId() {
        return orderId != null ? orderId : "-";
    }

    public String getUserId() {
        return userId != null ? userId : "-";
    }

    public String getItems() {
        return items != null ? items : "-";
    }

    public int getSubtotal() {
        return subtotal;
    }

    public int getOngkir() {
        return ongkir;
    }

    public int getTotal() {
        return total;
    }

    public String getStatus() {
        return status != null ? status : "pending";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNama() {
        return nama != null ? nama : "-";
    }

    public String getPhone() {
        return phone != null ? phone : "-";
    }

    public String getAddress() {
        return address != null ? address : "-";
    }

    public String getPayment() {
        return payment != null ? payment : "-";
    }

    public String getCourier() {
        return courier != null ? courier : "-";
    }

    public int getAdmin() {
        return admin;
    }

    // ================= SETTER =================

    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setItems(String items) { this.items = items; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public void setOngkir(int ongkir) { this.ongkir = ongkir; }
    public void setTotal(int total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setNama(String nama) { this.nama = nama; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }

    public void setPayment(String payment) { this.payment = payment; }
    public void setCourier(String courier) { this.courier = courier; }
    public void setAdmin(int admin) { this.admin = admin; }
}
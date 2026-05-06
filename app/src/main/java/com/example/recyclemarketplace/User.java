package com.example.recyclemarketplace;

public class User {

    public String nama, email;

    public User() {
        // wajib untuk Firebase
    }

    public User(String nama, String email) {
        this.nama = nama;
        this.email = email;
    }
}
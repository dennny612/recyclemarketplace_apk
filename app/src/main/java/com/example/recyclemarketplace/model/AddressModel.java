package com.example.recyclemarketplace.model;

public class AddressModel {

    public String nama, phone, provinsi, kota, kodePos, alamat;

    public AddressModel() {}

    public AddressModel(String nama, String phone, String provinsi,
                        String kota, String kodePos, String alamat) {
        this.nama = nama;
        this.phone = phone;
        this.provinsi = provinsi;
        this.kota = kota;
        this.kodePos = kodePos;
        this.alamat = alamat;
    }
}
package com.example.recyclemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclemarketplace.model.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class InformasiPribadiActivity extends AppCompatActivity {

    ImageView btnBack, imgMap;
    Button btnSave, btnOpenMap;

    EditText etNama, etPhone, etProvinsi, etKota, etKodePos, etAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_pribadi);

        btnBack = findViewById(R.id.btnBack);
        imgMap = findViewById(R.id.imgMap);
        btnSave = findViewById(R.id.btnSave);
        btnOpenMap = findViewById(R.id.btnOpenMap);

        etNama = findViewById(R.id.etNama);
        etPhone = findViewById(R.id.etPhone);
        etProvinsi = findViewById(R.id.etProvinsi);
        etKota = findViewById(R.id.etKota);
        etKodePos = findViewById(R.id.etKodePos);
        etAlamat = findViewById(R.id.etAlamat);

        btnBack.setOnClickListener(v -> finish());

        btnOpenMap.setOnClickListener(v -> bukaMaps());
        imgMap.setOnClickListener(v -> bukaMaps());

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {

        String nama = etNama.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String provinsi = etProvinsi.getText().toString().trim();
        String kota = etKota.getText().toString().trim();
        String kodePos = etKodePos.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();

        if (nama.isEmpty() || phone.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Lengkapi data dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        AddressModel data = new AddressModel(
                nama, phone, provinsi, kota, kodePos, alamat
        );

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("addressInfo")
                .setValue(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Alamat berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show();
                });
    }

    private void bukaMaps() {

        String alamat = etAlamat.getText().toString();

        if (alamat.isEmpty()) {
            Toast.makeText(this, "Isi alamat dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = "geo:0,0?q=" + Uri.encode(alamat);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }
}
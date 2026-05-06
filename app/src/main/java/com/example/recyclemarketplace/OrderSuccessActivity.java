package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {

    TextView tvItems, tvTotal, tvReceiver, tvOngkir, tvAdmin, tvPayment, tvCourier;
    Button btnDetail, btnHome;

    String items = "-", receiver = "-", payment = "-", courier = "-", address = "-", orderId = "-";
    int total = 0, ongkir = 0, admin = 0;

    NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        nf = NumberFormat.getInstance(new Locale("id", "ID"));

        initView();
        getData();
        setData();
        setupButton();
    }

    private void initView() {
        tvItems = findViewById(R.id.tvItems);
        tvTotal = findViewById(R.id.tvTotal);
        tvReceiver = findViewById(R.id.tvReceiver);
        tvOngkir = findViewById(R.id.tvOngkir);
        tvAdmin = findViewById(R.id.tvAdmin);
        tvPayment = findViewById(R.id.tvPayment);
        tvCourier = findViewById(R.id.tvCourier);

        btnDetail = findViewById(R.id.btnDetail);
        btnHome = findViewById(R.id.btnHome);
    }

    private void getData() {
        Intent i = getIntent();

        if (i != null) {
            items = safeString(i.getStringExtra("items"));
            receiver = safeString(i.getStringExtra("receiver"));
            payment = safeString(i.getStringExtra("payment"));
            courier = safeString(i.getStringExtra("courier"));
            address = safeString(i.getStringExtra("address"));
            orderId = safeString(i.getStringExtra("orderId"));

            total = i.getIntExtra("total", 0);
            ongkir = i.getIntExtra("ongkir", 0);
            admin = i.getIntExtra("admin", 0);

            // 🔥 DEBUG WAJIB (CEK DATA MASUK)
            Log.d("ORDER_SUCCESS", "ITEMS: " + items);
            Log.d("ORDER_SUCCESS", "TOTAL: " + total);
        }
    }

    private void setData() {
        tvItems.setText(items);
        tvTotal.setText("Rp " + nf.format(total));
        tvReceiver.setText("Penerima: " + receiver);
        tvPayment.setText("Metode: " + payment);
        tvCourier.setText("Kurir: " + courier);
        tvOngkir.setText("Ongkir: Rp " + nf.format(ongkir));
        tvAdmin.setText("Biaya Admin: Rp " + nf.format(admin));
    }

    private void setupButton() {

        btnDetail.setOnClickListener(v -> {

            // 🔥 VALIDASI LEBIH KETAT
            if (items == null || items.equals("-") || items.trim().isEmpty()) {
                Toast.makeText(this, "Data pesanan kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(OrderSuccessActivity.this, OrderDetailActivity.class);

            // 🔥 PASTIKAN SEMUA TERKIRIM
            intent.putExtra("items", items);
            intent.putExtra("receiver", receiver);
            intent.putExtra("address", address);
            intent.putExtra("orderId", orderId);
            intent.putExtra("total", total);
            intent.putExtra("ongkir", ongkir);
            intent.putExtra("admin", admin);
            intent.putExtra("payment", payment);
            intent.putExtra("courier", courier);

            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String safeString(String value) {
        return value != null ? value : "-";
    }
}
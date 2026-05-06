package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclemarketplace.model.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    TextView tvSubtotal, tvOngkir, tvTotal, tvItems, tvPrice, tvAdmin;
    EditText etNama, etPhone, etAddress;
    Spinner spCourier, spPayment;
    Button btnOrder;
    ImageView btnBack;

    int subtotal = 0;
    int ongkir = 12000;
    int biayaAdmin = 0;
    int totalFinal = 0;

    String items = "-";
    String uid;

    String selectedCourier = "J&T Express";
    String selectedPayment = "COD";

    NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initView();

        nf = NumberFormat.getInstance(new Locale("id", "ID"));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        getDataFromIntent();
        setupSpinner();
        calculateTotal();
        loadUserAddress(); // 🔥 PENTING (AUTO ISI ALAMAT)

        btnBack.setOnClickListener(v -> finish());
        btnOrder.setOnClickListener(v -> handleOrder());
    }

    private void initView() {
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvOngkir = findViewById(R.id.tvOngkir);
        tvTotal = findViewById(R.id.tvTotal);
        tvItems = findViewById(R.id.tvItems);
        tvPrice = findViewById(R.id.tvPrice);
        tvAdmin = findViewById(R.id.tvAdmin);

        etNama = findViewById(R.id.etNama);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        spCourier = findViewById(R.id.spCourier);
        spPayment = findViewById(R.id.spPayment);

        btnOrder = findViewById(R.id.btnOrder);
        btnBack = findViewById(R.id.btnBack);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            subtotal = intent.getIntExtra("total", 0);
            items = intent.getStringExtra("items");
        }

        if (items == null || items.trim().isEmpty()) {
            items = "-";
        }

        tvItems.setText(items);
        tvPrice.setText("Rp " + nf.format(subtotal));

        Log.d("CHECKOUT", "ITEMS = " + items);
    }

    private void setupSpinner() {

        String[] courierList = {"J&T Express", "JNE Express"};
        String[] paymentList = {"COD", "QRIS"};

        spCourier.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, courierList));

        spPayment.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, paymentList));

        spCourier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedCourier = courierList[pos];
                ongkir = (pos == 0) ? 12000 : 15000;

                calculateTotal();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedPayment = paymentList[pos];
                biayaAdmin = (pos == 0) ? 0 : 1000;

                calculateTotal();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calculateTotal() {
        totalFinal = subtotal + ongkir + biayaAdmin;

        tvSubtotal.setText("Total Harga: Rp " + nf.format(subtotal));
        tvOngkir.setText("Ongkir: Rp " + nf.format(ongkir));
        tvAdmin.setText("Biaya Admin: Rp " + nf.format(biayaAdmin));
        tvTotal.setText("Total Tagihan: Rp " + nf.format(totalFinal));
    }

    // 🔥 AUTO LOAD ALAMAT DARI FIREBASE
    private void loadUserAddress() {

        if (uid == null) return;

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("addressInfo")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        String nama = snapshot.child("nama").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String alamat = snapshot.child("alamat").getValue(String.class);

                        if (nama != null && etNama.getText().toString().isEmpty()) {
                            etNama.setText(nama);
                        }

                        if (phone != null && etPhone.getText().toString().isEmpty()) {
                            etPhone.setText(phone);
                        }

                        if (alamat != null && etAddress.getText().toString().isEmpty()) {
                            etAddress.setText(alamat);
                        }
                    }
                });
    }

    private void handleOrder() {

        String nama = etNama.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (nama.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Lengkapi data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (items.equals("-")) {
            Toast.makeText(this, "Item kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(uid)
                .push()
                .getKey();

        if (orderId == null) {
            Toast.makeText(this, "Gagal membuat order", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderModel order = new OrderModel(
                orderId,
                uid,
                items,
                subtotal,
                ongkir,
                totalFinal,
                "pending",
                System.currentTimeMillis(),
                nama,
                phone,
                address,
                selectedPayment,
                selectedCourier,
                biayaAdmin
        );

        FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(uid)
                .child(orderId)
                .setValue(order)
                .addOnSuccessListener(unused -> {

                    Intent i = new Intent(this, OrderSuccessActivity.class);

                    i.putExtra("items", items);
                    i.putExtra("total", totalFinal);
                    i.putExtra("receiver", nama);
                    i.putExtra("address", address);
                    i.putExtra("orderId", orderId);
                    i.putExtra("payment", selectedPayment);
                    i.putExtra("courier", selectedCourier);
                    i.putExtra("ongkir", ongkir);
                    i.putExtra("admin", biayaAdmin);

                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan order", Toast.LENGTH_SHORT).show();
                });
    }
}
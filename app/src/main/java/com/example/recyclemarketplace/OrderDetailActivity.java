package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclemarketplace.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    LinearLayout layoutItems;
    TextView tvTotal, tvSubtotal, tvShipping, tvDiscount,
            tvAddress, tvOrderId, tvDate, tvStatus,
            tvPayment, tvCourier, tvAdmin;

    Button btnBuyAgain, btnRate;

    NumberFormat nf;

    String items = "-", address = "-", receiver = "-", orderId = "-";
    String payment = "-", courier = "-";

    int total = 0, ongkir = 0, admin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();
        getData();
        setData();
        setupButton();
    }

    private void initView() {
        layoutItems = findViewById(R.id.layoutItems);

        tvTotal = findViewById(R.id.tvTotal);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvAddress = findViewById(R.id.tvAddress);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);

        tvPayment = findViewById(R.id.tvPayment);
        tvCourier = findViewById(R.id.tvCourier);
        tvAdmin = findViewById(R.id.tvAdmin);

        btnBuyAgain = findViewById(R.id.btnBuyAgain);
        btnRate = findViewById(R.id.btnRate);

        nf = NumberFormat.getInstance(new Locale("id", "ID"));
    }

    private void getData() {
        if (getIntent() != null) {

            items = safe(getIntent().getStringExtra("items"));
            address = safe(getIntent().getStringExtra("address"));
            receiver = safe(getIntent().getStringExtra("receiver"));
            orderId = safe(getIntent().getStringExtra("orderId"));

            payment = safe(getIntent().getStringExtra("payment"));
            courier = safe(getIntent().getStringExtra("courier"));

            total = getIntent().getIntExtra("total", 0);
            ongkir = getIntent().getIntExtra("ongkir", 0);
            admin = getIntent().getIntExtra("admin", 0);
        }
    }

    private void setData() {

        tvOrderId.setText("ORDER ID: " + orderId);
        tvDate.setText("Hari ini");
        tvStatus.setText("Diproses");

        tvAddress.setText(receiver + "\n" + address);

        tvPayment.setText("Metode: " + payment);
        tvCourier.setText("Kurir: " + courier);
        tvAdmin.setText("Biaya Admin: Rp " + nf.format(admin));

        layoutItems.removeAllViews();

        if (!items.equals("-")) {
            String[] list = items.split("\n");

            for (String item : list) {
                if (!item.trim().isEmpty()) {
                    addItem(item);
                }
            }
        }

        int subtotal = total - ongkir - admin;
        if (subtotal < 0) subtotal = 0;

        tvSubtotal.setText("Subtotal: Rp " + nf.format(subtotal));
        tvShipping.setText("Ongkir: Rp " + nf.format(ongkir));
        tvDiscount.setText("Diskon: Rp 0");
        tvTotal.setText("Total: Rp " + nf.format(total));
    }

    private void setupButton() {

        // 🔥 BUY AGAIN → MASUK KE CART (SUDAH SESUAI CART MODEL)
        btnBuyAgain.setOnClickListener(v -> {

            if (items.equals("-")) {
                Toast.makeText(this, "Item kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getUid();

            if (uid == null) {
                Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String[] list = items.split("\n");

                for (String item : list) {

                    String[] parts = item.split("\\|");

                    if (parts.length < 3) continue;

                    String nama = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    int harga = Integer.parseInt(parts[2]);

                    // 🔥 GENERATE ID
                    String cartId = FirebaseDatabase.getInstance()
                            .getReference("cart")
                            .child(uid)
                            .push()
                            .getKey();

                    // 🔥 BUAT OBJECT SESUAI MODEL
                    CartModel cart = new CartModel(
                            cartId,
                            nama,
                            harga,
                            qty,
                            "",     // imgUrl (kosong dulu)
                            true    // checked default
                    );

                    // 🔥 SIMPAN KE FIREBASE
                    FirebaseDatabase.getInstance()
                            .getReference("cart")
                            .child(uid)
                            .child(cartId)
                            .setValue(cart);
                }

                Toast.makeText(this, "Berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 🔥 RATE → KE REVIEW
        btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("items", items);
            startActivity(intent);
        });
    }

    private void addItem(String itemText) {

        try {
            String[] parts = itemText.split("\\|");

            if (parts.length < 3) return;

            String nama = parts[0];
            int qty = Integer.parseInt(parts[1]);
            int harga = Integer.parseInt(parts[2]);

            int totalItem = qty * harga;

            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_order, layoutItems, false);

            TextView tvItem = view.findViewById(R.id.tvItems);
            TextView tvHarga = view.findViewById(R.id.tvTotal);
            TextView tvStatusItem = view.findViewById(R.id.tvStatus);
            TextView tvDateItem = view.findViewById(R.id.tvDate);

            tvItem.setText(nama + " x" + qty);
            tvHarga.setText("Rp " + nf.format(totalItem));
            tvStatusItem.setText("Status: Diproses");
            tvDateItem.setText("Hari ini");

            layoutItems.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safe(String s) {
        return s != null ? s : "-";
    }
}
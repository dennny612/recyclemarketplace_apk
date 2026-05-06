package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.adapter.CartAdapter;
import com.example.recyclemarketplace.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvTotal, tvItemCount; // 🔥 TAMBAHAN
    private ImageView btnBack;
    private Button btnCheckout;

    private CartAdapter adapter;
    private List<CartModel> list;

    private DatabaseReference db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);
        tvItemCount = findViewById(R.id.tvItemCount); // 🔥 INIT
        btnBack = findViewById(R.id.btnBack);
        btnCheckout = findViewById(R.id.btnCheckout);

        btnBack.setOnClickListener(v -> finish());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        list = new ArrayList<>();
        adapter = new CartAdapter(this, list);

        // 🔥 LISTENER SAAT CHECKBOX BERUBAH
        adapter.setCartUpdateListener(() -> {
            hitungTotal();
            hitungItem();
        });

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> handleCheckout());

        loadData();
    }

    private void handleCheckout() {

        int total = 0;
        StringBuilder itemsBuilder = new StringBuilder();

        for (CartModel item : list) {

            if (item.isChecked()) {

                int itemTotal = item.getTotalPrice();
                total += itemTotal;

                itemsBuilder.append(item.getNama())
                        .append("|")
                        .append(item.getQty())
                        .append("|")
                        .append(itemTotal)
                        .append("\n");
            }
        }

        if (total == 0) {
            Toast.makeText(this, "Pilih produk dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        intent.putExtra("total", total);
        intent.putExtra("items", itemsBuilder.toString());

        startActivity(intent);
    }

    private void loadData() {

        if (uid == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        db = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(uid);

        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    CartModel item = data.getValue(CartModel.class);

                    if (item != null) {
                        item.setId(data.getKey());
                        list.add(item);
                    }
                }

                adapter.notifyDataSetChanged();

                // 🔥 UPDATE SEMUA
                hitungTotal();
                hitungItem();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CartActivity.this,
                        "Gagal load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 HITUNG TOTAL HARGA
    private void hitungTotal() {

        int total = 0;

        for (CartModel item : list) {
            if (item.isChecked()) {
                total += item.getTotalPrice();
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        tvTotal.setText("Rp " + nf.format(total));
    }

    // 🔥 HITUNG JUMLAH ITEM (INI YANG KAMU BUTUH)
    private void hitungItem() {

        int totalItem = 0;

        for (CartModel item : list) {
            if (item.isChecked()) {
                totalItem += item.getQty(); // kalau ga ada qty → pakai 1
            }
        }

        tvItemCount.setText(totalItem + " item");
    }
}
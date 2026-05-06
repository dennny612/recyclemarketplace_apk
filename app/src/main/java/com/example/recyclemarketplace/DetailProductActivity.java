package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.recyclemarketplace.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProductActivity extends AppCompatActivity {

    ImageView ivProduct;
    TextView tvName, tvPrice, tvQty, tvTotal;
    Button btnPlus, btnMinus, btnCheckout;

    int qty = 1;
    String id, name, imgUrl;
    int price;

    NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        ivProduct = findViewById(R.id.ivProduct);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvQty = findViewById(R.id.tvQty);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnCheckout = findViewById(R.id.btnCheckout);

        nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // 🔥 AMBIL DATA (WAJIB TAMBAH ID)
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        price = getIntent().getIntExtra("price", 0);
        imgUrl = getIntent().getStringExtra("image");

        if (name == null) name = "Produk";
        if (imgUrl == null) imgUrl = "";
        if (id == null) id = String.valueOf(System.currentTimeMillis());

        tvName.setText(name);
        tvPrice.setText("Rp " + nf.format(price));
        tvQty.setText(String.valueOf(qty));

        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivProduct);

        updateTotal();

        // ➕
        btnPlus.setOnClickListener(v -> {
            qty++;
            tvQty.setText(String.valueOf(qty));
            updateTotal();
        });

        // ➖
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                tvQty.setText(String.valueOf(qty));
                updateTotal();
            }
        });

        // 🛒 CHECKOUT
        btnCheckout.setOnClickListener(v -> addToCart());
    }

    private void updateTotal() {
        int total = price * qty;
        tvTotal.setText("Total: Rp " + nf.format(total));
    }

    private void addToCart() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Silakan login dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(uid)
                .child(id); // 🔥 pakai ID produk (bukan random)

        CartModel cart = new CartModel(
                id,
                name,
                price,
                qty,
                imgUrl,
                true
        );

        db.setValue(cart)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "Masuk ke keranjang 🛒", Toast.LENGTH_SHORT).show();

                    // 🔥 PINDAH KE CART
                    startActivity(new Intent(this, CartActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
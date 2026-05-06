package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView tvRevenue, tvOrders, tvAdmin;
    LinearLayout btnOrders, btnProduct, btnReport, btnLogout;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // 🔗 INIT VIEW
        tvRevenue = findViewById(R.id.tvRevenue);
        tvOrders  = findViewById(R.id.tvOrders);
        tvAdmin   = findViewById(R.id.tvAdmin);

        btnOrders  = findViewById(R.id.btnOrders);
        btnProduct = findViewById(R.id.btnProduct);
        btnReport  = findViewById(R.id.btnReport);
        btnLogout  = findViewById(R.id.btnLogout);

        // 🔐 AMANIN USER LOGIN
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            tvAdmin.setText(email);
        } else {
            tvAdmin.setText("Admin");
        }

        loadDashboard();

        // 📊 KE REPORT
        btnReport.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReportActivity.class));
        });

        // ➕ KE TAMBAH PRODUK
        btnProduct.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AddProductActivity.class));
        });

        // 📦 KE KELOLA PESANAN (INI YANG KAMU MINTA)
        btnOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
        });

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadDashboard() {

        db = FirebaseDatabase.getInstance().getReference("orders");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int totalOrder = 0;
                int totalRevenue = 0;

                for (DataSnapshot user : snapshot.getChildren()) {
                    for (DataSnapshot orderSnap : user.getChildren()) {

                        Integer total = orderSnap.child("total").getValue(Integer.class);

                        if (total != null) {
                            totalRevenue += total;
                        }

                        totalOrder++;
                    }
                }

                tvOrders.setText(String.valueOf(totalOrder));
                tvRevenue.setText("Rp " + totalRevenue);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Gagal load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
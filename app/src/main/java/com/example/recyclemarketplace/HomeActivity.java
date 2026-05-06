package com.example.recyclemarketplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recyclemarketplace.adapter.ProductAdapter;
import com.example.recyclemarketplace.model.ProductModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout catPlastik, catKartas, catKaca, catKain, catSuka;
    private ImageView ivCart;

    private ProductAdapter productAdapter;
    private List<ProductModel> productList = new ArrayList<>();

    FirebaseAuth mAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupCategoryClicks();
        setupCartClick();

        // 🔥 TAMBAHAN FIREBASE
        db = FirebaseDatabase.getInstance().getReference("products");
        loadProductsFromFirebase();
    }

    private void initViews() {
        rvProducts = findViewById(R.id.rvProducts);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        catPlastik = findViewById(R.id.catPlastik);
        catKartas = findViewById(R.id.catKartas);
        catKaca = findViewById(R.id.catKaca);
        catKain = findViewById(R.id.catKain);
        catSuka = findViewById(R.id.catSuka);
        ivCart = findViewById(R.id.ivCart);
    }

    private void setupCartClick() {
        ivCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );
    }

    // 🔥 AMBIL DATA DARI FIREBASE
    private void loadProductsFromFirebase() {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                productList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    ProductModel product = data.getValue(ProductModel.class);

                    if (product != null) {
                        productList.add(product);
                    }
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this,
                        "Gagal load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {

            @Override
            public void onProductClick(ProductModel product, int position) {

                Intent intent = new Intent(HomeActivity.this, DetailProductActivity.class);
                intent.putExtra("name", product.getNama());
                intent.putExtra("price", product.getHarga());
                intent.putExtra("image", product.getImgUrl());

                startActivity(intent);
            }

            @Override
            public void onWishlistClick(ProductModel product, int position) {
                Toast.makeText(HomeActivity.this,
                        product.getNama() + " ditambahkan ke favorit ❤️",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) return true;

            else if (id == R.id.nav_market) {
                startActivity(new Intent(this, MarketPlaceActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    private void setupCategoryClicks() {
        catPlastik.setOnClickListener(v -> openCategory("Plastik"));
        catKartas.setOnClickListener(v -> openCategory("Kertas"));
        catKaca.setOnClickListener(v -> openCategory("Kaca"));
        catKain.setOnClickListener(v -> openCategory("Kain"));
        catSuka.setOnClickListener(v -> openCategory("Favorit"));
    }

    private void openCategory(String name) {

        Intent intent = new Intent(HomeActivity.this, MarketPlaceActivity.class);
        intent.putExtra("category", name);

        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
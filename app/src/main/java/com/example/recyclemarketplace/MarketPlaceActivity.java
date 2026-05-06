package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.adapter.MarketProductAdapter;
import com.example.recyclemarketplace.model.MarketProductModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceActivity extends AppCompatActivity {

    private RecyclerView rvMarketProducts;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private TextView chipSemua, chipPlastik, chipKertas, chipKaca, chipKain;

    private MarketProductAdapter adapter;
    private List<MarketProductModel> allProducts = new ArrayList<>();
    private List<MarketProductModel> filteredList = new ArrayList<>();

    private String activeChip = "Semua";

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        initViews();
        setupRecyclerView();
        setupChips();
        setupSearch();
        setupBottomNavigation();

        // 🔥 CONNECT FIREBASE
        db = FirebaseDatabase.getInstance().getReference("products");
        loadProducts();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void initViews() {
        rvMarketProducts = findViewById(R.id.rvMarketProducts);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        etSearch = findViewById(R.id.etSearch);
        chipSemua = findViewById(R.id.chipSemua);
        chipPlastik = findViewById(R.id.chipPlastik);
        chipKertas = findViewById(R.id.chipKertas);
        chipKaca = findViewById(R.id.chipKaca);
        chipKain = findViewById(R.id.chipKain);
    }

    // 🔥 LOAD DATA DARI FIREBASE
    private void loadProducts() {

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                allProducts.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    MarketProductModel product = data.getValue(MarketProductModel.class);

                    if (product != null) {
                        allProducts.add(product);
                    }
                }

                applyFilter(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MarketPlaceActivity.this,
                        "Gagal load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {

        rvMarketProducts.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MarketProductAdapter(this, filteredList);
        rvMarketProducts.setAdapter(adapter);

        adapter.setOnItemClickListener(new MarketProductAdapter.OnItemClickListener() {
            @Override
            public void onProductClick(MarketProductModel product, int position) {

                // 🔥 PINDAH KE DETAIL
                Intent intent = new Intent(MarketPlaceActivity.this, DetailProductActivity.class);

                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("image", product.getImage());
                intent.putExtra("desc", product.getDesc());

                startActivity(intent);
            }

            @Override
            public void onWishlistClick(MarketProductModel product, int position) {
                Toast.makeText(MarketPlaceActivity.this,
                        product.getName() + " ditambahkan ke favorit ❤️", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChips() {
        chipSemua.setOnClickListener(v -> filterByChip("Semua", chipSemua));
        chipPlastik.setOnClickListener(v -> filterByChip("Plastik", chipPlastik));
        chipKertas.setOnClickListener(v -> filterByChip("Kertas", chipKertas));
        chipKaca.setOnClickListener(v -> filterByChip("Kaca", chipKaca));
        chipKain.setOnClickListener(v -> filterByChip("Kain", chipKain));
    }

    private void filterByChip(String category, TextView selectedChip) {

        activeChip = category;

        TextView[] chips = {chipSemua, chipPlastik, chipKertas, chipKaca, chipKain};

        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_inactive);
            chip.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        selectedChip.setBackgroundResource(R.drawable.bg_chip_active);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));

        applyFilter(etSearch.getText().toString());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilter(String keyword) {

        filteredList.clear();

        for (MarketProductModel product : allProducts) {

            boolean matchCategory = activeChip.equals("Semua") ||
                    (product.getCategory() != null &&
                            product.getCategory().equalsIgnoreCase(activeChip));

            boolean matchKeyword = keyword.isEmpty() ||
                    (product.getName() != null &&
                            product.getName().toLowerCase().contains(keyword.toLowerCase()));

            if (matchCategory && matchKeyword) {
                filteredList.add(product);
            }
        }

        adapter.updateList(filteredList);
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_market);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_market) {
                return true;

            } else if (id == R.id.nav_chat) {

                Intent intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_profile) {

                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }
}
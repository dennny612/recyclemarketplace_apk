package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.adapter.ChatAdapter;
import com.example.recyclemarketplace.model.ChatModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatModel> list;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 🔥 Hilangkan animasi biar tidak delay
        getWindow().setWindowAnimations(0);

        initViews();
        setupRecyclerView();
        setupDummyData();
        setupBottomNavigation();
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        list = new ArrayList<>();
        adapter = new ChatAdapter(list);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setHasFixedSize(true);      // 🔥 anti lag
        rvChat.setItemAnimator(null);      // 🔥 hilangkan animasi
        rvChat.setAdapter(adapter);
    }

    private void setupDummyData() {
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // 🏠 HOME
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            // 🛒 MARKET
            else if (id == R.id.nav_market) {
                startActivity(new Intent(this, MarketPlaceActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            // 💬 CHAT (tidak reload)
            else if (id == R.id.nav_chat) {
                return true;
            }

            // 👤 PROFILE
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }
}
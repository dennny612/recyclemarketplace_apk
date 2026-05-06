package com.example.recyclemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

// FIREBASE
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar, btnEditAvatar;
    private TextView tvUsername, tvPlasticSaved;
    private LinearLayout layoutHeader;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnSettings;
    private BottomNavigationView bottomNavigationView;

    private Uri imageUri;
    private String uid;

    // 🔥 TAB SUDAH DIGANTI
    private static final String[] TAB_TITLES = {"Ulasan Saya", "Riwayat Pembelian"};
    private static final int REQUEST_CODE_AVATAR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupTabs();
        setupClickListeners();
        setupBottomNavigation();
        loadUserProfile();
    }

    private void initViews() {
        ivAvatar       = findViewById(R.id.ivAvatar);
        btnEditAvatar  = findViewById(R.id.btnEditAvatar);
        tvUsername     = findViewById(R.id.tvUsername);
        tvPlasticSaved = findViewById(R.id.tvPlasticSaved);

        layoutHeader   = findViewById(R.id.layoutHeader);

        tabLayout      = findViewById(R.id.tabLayout);
        viewPager      = findViewById(R.id.viewPager);
        btnSettings    = findViewById(R.id.btnSettings);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupTabs() {
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }

    private void setupClickListeners() {

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));

        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_AVATAR);
        });
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            }

            else if (id == R.id.nav_market) {
                startActivity(new Intent(this, MarketPlaceActivity.class));
                finish();
                return true;
            }

            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                finish();
                return true;
            }

            return id == R.id.nav_profile;
        });
    }

    // 🔥 LOAD PROFILE
    private void loadUserProfile() {

        if (uid == null) {
            tvUsername.setText("User tidak ditemukan");
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

        database.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String nama = snapshot.child("nama").getValue(String.class);
                    String avatarUrl = snapshot.child("avatar").getValue(String.class);

                    String finalNama = (nama != null && !nama.isEmpty()) ? nama : "User";

                    tvUsername.setText(finalNama);

                    layoutHeader.setBackgroundColor(
                            ContextCompat.getColor(ProfileActivity.this, android.R.color.white)
                    );

                    tvPlasticSaved.setText("Selamat datang, " + finalNama + " 👋");

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_avatar_placeholder)
                                .error(R.drawable.ic_avatar_placeholder)
                                .into(ivAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AVATAR && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivAvatar.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {

        if (uid == null || imageUri == null) return;

        String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("avatars/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                databaseRef().child(uid).child("avatar").setValue(uri.toString())
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload gagal", Toast.LENGTH_SHORT).show()
                );
    }

    private DatabaseReference databaseRef() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    // 🔥 PAGER ADAPTER (SUDAH DIGANTI KE REVIEW)
    private static class ProfilePagerAdapter extends FragmentStateAdapter {

        public ProfilePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new ReviewFragment(); // 🔥 ULASAN SAYA
            } else {
                return new RiwayatPembelianFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
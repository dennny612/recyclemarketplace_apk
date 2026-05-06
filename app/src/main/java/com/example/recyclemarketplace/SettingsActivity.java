package com.example.recyclemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout btnLogout, btnProfile, btnLanguage, btnHelp, btnWebsite;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // INIT VIEW
        btnBack     = findViewById(R.id.btnBack);
        btnLogout   = findViewById(R.id.btnLogout);
        btnProfile  = findViewById(R.id.btnProfile);
        btnLanguage = findViewById(R.id.btnLanguage);
        btnHelp     = findViewById(R.id.btnHelp);
        btnWebsite  = findViewById(R.id.btnWebsite);

        // INIT GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 🔙 BACK
        btnBack.setOnClickListener(v -> finish());

        // 🌐 WEBSITE
        btnWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://dennny612.github.io/RecylceMarketplace/"));
            startActivity(intent);
        });

        // 👤 PROFILE (🔥 FIX UTAMA DI SINI)
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, InformasiPribadiActivity.class);
            startActivity(intent);
        });

        // 🌐 BAHASA
        btnLanguage.setOnClickListener(v ->
                Toast.makeText(this, "Fitur bahasa belum tersedia", Toast.LENGTH_SHORT).show()
        );

        // ❓ BANTUAN
        btnHelp.setOnClickListener(v ->
                Toast.makeText(this, "Pusat Bantuan", Toast.LENGTH_SHORT).show()
        );

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {

        // 1. Logout Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Clear session lokal
        getSharedPreferences("USER", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // 3. Logout Google
        googleSignInClient.signOut().addOnCompleteListener(task -> {

            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();

            // 4. Pindah ke Login (clear semua activity)
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });
    }
}
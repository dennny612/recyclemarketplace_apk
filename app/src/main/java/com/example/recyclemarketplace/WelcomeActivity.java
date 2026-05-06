package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int SPLASH_DELAY = 1000; // 1 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // 🔥 Delay biar smooth kayak splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 🔐 CEK LOGIN
            if (mAuth.getCurrentUser() != null) {
                // SUDAH LOGIN → LANGSUNG HOME
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            } else {
                // BELUM LOGIN → TAMPILKAN HALAMAN
                setContentView(R.layout.activity_welcome);

                Button btnLogin = findViewById(R.id.btnLogin);
                Button btnSignup = findViewById(R.id.btnSignup);

                btnLogin.setOnClickListener(v ->
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class))
                );

                btnSignup.setOnClickListener(v ->
                        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class))
                );
            }

        }, SPLASH_DELAY);
    }
}
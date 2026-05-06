package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_TIME = 5000;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                // ✅ SUDAH LOGIN → KE HOME
                startActivity(new Intent(Splash.this, HomeActivity.class));
            } else {
                // ❌ BELUM LOGIN → KE WELCOME / LOGIN
                startActivity(new Intent(Splash.this, WelcomeActivity.class));
            }

            finish();

        }, SPLASH_TIME);
    }
}
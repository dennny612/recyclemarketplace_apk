package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister, btnGoogle;
    TextView txtForgot;
    EditText etUsername, etPassword;
    ImageView ivTogglePassword;

    boolean isPasswordVisible = false;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // 🔥 AUTO LOGIN
        if (mAuth.getCurrentUser() != null) {
            checkUserRoleAndRedirect(mAuth.getCurrentUser().getUid());
            return;
        }

        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtForgot = findViewById(R.id.txtForgot);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // 🔥 GOOGLE CONFIG
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 🔐 TOGGLE PASSWORD
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        // 🔐 LOGIN EMAIL
        btnLogin.setOnClickListener(v -> {

            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etUsername.setError("Email tidak boleh kosong");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password tidak boleh kosong");
                return;
            }

            loginFirebase(email, password);
        });

        // 🔥 GOOGLE LOGIN
        btnGoogle.setOnClickListener(v ->
                startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN)
        );

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ResetPasswordActivity.class)));
    }

    // 🔐 LOGIN EMAIL
    private void loginFirebase(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();
                        checkUserRoleAndRedirect(uid);

                    } else {
                        Toast.makeText(this,
                                "Email atau password salah",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 🔥 CEK ROLE → REDIRECT
    private void checkUserRoleAndRedirect(String uid) {

        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String role = snapshot.child("role").getValue(String.class);

                if ("admin".equals(role)) {
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }

                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this,
                        "Gagal cek role", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 RESULT GOOGLE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);

                firebaseAuthWithGoogle(account);

            } catch (Exception e) {
                Toast.makeText(this, "Google login gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 🔥 GOOGLE AUTH FINAL (FIX ROLE)
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();
                        String email = account.getEmail();
                        String nama = account.getDisplayName();

                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(uid);

                        // 🔥 CEK USER ADA / BELUM
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    // USER BARU
                                    userRef.child("email").setValue(email);
                                    userRef.child("nama").setValue(nama);
                                    userRef.child("role").setValue("user");
                                }

                                checkUserRoleAndRedirect(uid);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(LoginActivity.this,
                                        "Gagal ambil data user", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(this, "Google Auth gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
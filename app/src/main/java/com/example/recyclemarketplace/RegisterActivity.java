package com.example.recyclemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama, etEmail, etPassword, etKonfirmasi;
    Button btnRegister;
    TextView tvLogin;
    CheckBox cbAgree;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // INIT VIEW
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etKonfirmasi = findViewById(R.id.etKonfirmasi);
        cbAgree = findViewById(R.id.cbAgree);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {

        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasi = etKonfirmasi.getText().toString().trim();

        // VALIDASI
        if (TextUtils.isEmpty(nama)) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(konfirmasi)) {
            etKonfirmasi.setError("Password tidak sama");
            etKonfirmasi.requestFocus();
            return;
        }

        if (!cbAgree.isChecked()) {
            Toast.makeText(this, "Setujui syarat & ketentuan", Toast.LENGTH_SHORT).show();
            return;
        }

        // REGISTER KE FIREBASE AUTH
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser == null) {
                            Toast.makeText(this, "Gagal mendapatkan user", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        // BUAT OBJECT USER
                        User user = new User(nama, email);

                        // SIMPAN KE REALTIME DATABASE
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(uid)
                                .setValue(user)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(this,
                                            "Register berhasil & data tersimpan",
                                            Toast.LENGTH_SHORT).show();

                                    // PINDAH KE LOGIN
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Gagal simpan data: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(this,
                                "Register gagal: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
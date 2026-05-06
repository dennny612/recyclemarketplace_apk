package com.example.recyclemarketplace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etEmail, etOldPassword, etNewPassword, etConfirmPassword;
    Button btnReset;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Inisialisasi
        etEmail = findViewById(R.id.etEmail);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnReset = findViewById(R.id.btnReset);

        database = FirebaseDatabase.getInstance().getReference("users");

        btnReset.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // ================= VALIDASI =================
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }

            if (TextUtils.isEmpty(oldPassword)) {
                etOldPassword.setError("Password lama tidak boleh kosong");
                return;
            }

            if (TextUtils.isEmpty(newPassword)) {
                etNewPassword.setError("Password baru tidak boleh kosong");
                return;
            }

            if (newPassword.length() < 6) {
                etNewPassword.setError("Minimal 6 karakter");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Password tidak cocok");
                return;
            }

            // ================= CEK KE FIREBASE =================
            database.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                boolean passwordCocok = false;

                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                                    // 🔐 cek password lama
                                    if (dbPassword != null && dbPassword.equals(oldPassword)) {

                                        passwordCocok = true;

                                        String userId = userSnapshot.getKey();

                                        // 🔥 update password baru
                                        database.child(userId)
                                                .child("password")
                                                .setValue(newPassword);

                                        Toast.makeText(ResetPasswordActivity.this,
                                                "Password berhasil diubah ✅",
                                                Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                }

                                if (!passwordCocok) {
                                    Toast.makeText(ResetPasswordActivity.this,
                                            "Password lama salah ❌",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(ResetPasswordActivity.this,
                                        "Email tidak ditemukan ❌",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
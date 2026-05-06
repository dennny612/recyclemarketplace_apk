package com.example.recyclemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import java.util.UUID;

public class SellerActivity extends AppCompatActivity {

    private EditText etProductName, etProductPrice, etProductDesc;
    private Spinner spinnerCategory;
    private ImageView ivPreview;
    private Button btnPickImage, btnTambahProduk, btnLogout;
    private RecyclerView rvAdminProducts;

    private Uri selectedImageUri = null;
    private DatabaseReference dbProducts;
    private StorageReference storageRef;

    private final String[] categories = {
            "Plastik", "Kertas", "Kaca", "Kain", "Lainnya"
    };

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).centerCrop().into(ivPreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ CEK ROLE PENJUAL
        String role = getSharedPreferences("USER", MODE_PRIVATE)
                .getString("role", "user");

        if (!role.equals("penjual")) {
            Toast.makeText(this, "Akses hanya untuk penjual!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_seller);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        dbProducts = FirebaseDatabase.getInstance().getReference("products");
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        initViews();
        setupSpinner();
        setupButtons();
        loadProducts();
    }

    private void initViews() {
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductDesc = findViewById(R.id.etProductDesc);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivPreview = findViewById(R.id.ivPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        btnLogout = findViewById(R.id.btnLogout);
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupButtons() {

        btnPickImage.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        btnTambahProduk.setOnClickListener(v -> {

            String name = etProductName.getText().toString().trim();
            String priceStr = etProductPrice.getText().toString().trim();
            String desc = etProductDesc.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            if (name.isEmpty()) {
                etProductName.setError("Nama wajib diisi");
                return;
            }

            if (priceStr.isEmpty()) {
                etProductPrice.setError("Harga wajib diisi");
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Pilih gambar dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            uploadProduct(name, price, desc, category);
        });

        // ✅ LOGOUT FIX TOTAL
        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            getSharedPreferences("USER", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void uploadProduct(String name, int price, String desc, String category) {

        btnTambahProduk.setEnabled(false);
        btnTambahProduk.setText("Uploading...");

        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storageRef.child(fileName);

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {

                            String id = dbProducts.push().getKey();

                            if (id == null) return;

                            java.util.HashMap<String, Object> product = new java.util.HashMap<>();
                            product.put("name", name);
                            product.put("price", price);
                            product.put("description", desc);
                            product.put("category", category);
                            product.put("imageUrl", uri.toString());

                            dbProducts.child(id).setValue(product);

                            Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            clearForm();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload gagal", Toast.LENGTH_SHORT).show();
                });

        btnTambahProduk.setEnabled(true);
        btnTambahProduk.setText("Simpan Produk");
    }

    private void clearForm() {
        etProductName.setText("");
        etProductPrice.setText("");
        etProductDesc.setText("");
        ivPreview.setImageResource(R.drawable.img_placeholder);
        selectedImageUri = null;
    }

    private void loadProducts() {
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));

        dbProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                java.util.List<String> list = new java.util.ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = data.child("name").getValue(String.class);
                    list.add(name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        SellerActivity.this,
                        android.R.layout.simple_list_item_1,
                        list
                );

                rvAdminProducts.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
                        TextView tv = new TextView(SellerActivity.this);
                        tv.setPadding(20,20,20,20);
                        return new RecyclerView.ViewHolder(tv) {};
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                        ((TextView) holder.itemView).setText(list.get(position));
                    }

                    @Override
                    public int getItemCount() {
                        return list.size();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
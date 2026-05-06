package com.example.recyclemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class AddProductActivity extends AppCompatActivity {

    EditText etName, etPrice, etStock, etDesc;
    Spinner spCategory;
    Button btnUpload, btnAddPhoto;
    ImageView imgPreview;

    Uri imageUri;
    DatabaseReference db;

    private static final int PICK_IMAGE = 1;

    // 🔥 SESUAIKAN DENGAN CLOUDINARY KAMU
    private static final String CLOUD_NAME = "drg2nqey2";
    private static final String UPLOAD_PRESET = "recyclemarketplace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etDesc = findViewById(R.id.etDesc);
        spCategory = findViewById(R.id.spCategory);
        btnUpload = findViewById(R.id.btnUpload);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        imgPreview = findViewById(R.id.imgPreview);

        db = FirebaseDatabase.getInstance().getReference("products");

        // Spinner kategori
        String[] kategori = {"Plastik", "Kertas", "Kain", "Kaca"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, kategori);
        spCategory.setAdapter(adapter);

        // Pilih gambar
        btnAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Upload
        btnUpload.setOnClickListener(v -> uploadProduct());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    private void uploadProduct() {

        String name = etName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String stock = etStock.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty() || stock.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Lengkapi data & pilih gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), bytes);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            RequestBody preset =
                    RequestBody.create(MediaType.parse("text/plain"), UPLOAD_PRESET);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.cloudinary.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CloudinaryService service = retrofit.create(CloudinaryService.class);

            service.uploadImage(body, preset).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                    Log.d("UPLOAD", "CODE: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getSecureUrl();

                        Log.d("UPLOAD", "URL: " + imageUrl);

                        saveToFirebase(name, price, stock, desc, category, imageUrl);

                    } else {
                        Toast.makeText(AddProductActivity.this,
                                "Upload gagal (response kosong)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.e("UPLOAD", "ERROR: " + t.getMessage());
                    Toast.makeText(AddProductActivity.this,
                            "Upload gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFirebase(String name, String price, String stock,
                                String desc, String category, String imageUrl) {

        String id = db.push().getKey();

        HashMap<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("price", Integer.parseInt(price));
        data.put("stock", Integer.parseInt(stock));
        data.put("desc", desc);
        data.put("category", category);
        data.put("image", imageUrl);

        db.child(id).setValue(data);

        Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        finish();
    }

    // =========================
    // CLOUDINARY API
    // =========================
    public interface CloudinaryService {
        @Multipart
        @POST("v1_1/" + CLOUD_NAME + "/image/upload")
        Call<UploadResponse> uploadImage(
                @Part MultipartBody.Part file,
                @Part("upload_preset") RequestBody preset
        );
    }

    // =========================
    // RESPONSE MODEL
    // =========================
    public class UploadResponse {
        private String secure_url;

        public String getSecureUrl() {
            return secure_url;
        }
    }
}
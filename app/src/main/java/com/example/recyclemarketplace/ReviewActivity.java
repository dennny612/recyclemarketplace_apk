package com.example.recyclemarketplace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.recyclemarketplace.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView tvRatingText, tvProductName;
    private EditText etReview;
    private Button btnSubmit;
    private ImageView btnBack, ivProduct;

    private String name, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initView();
        getIntentData();
        setupRating();
        setupButton();
    }

    private void initView() {

        ratingBar = findViewById(R.id.ratingBar);
        tvRatingText = findViewById(R.id.tvRatingText);
        tvProductName = findViewById(R.id.tvProductName);

        etReview = findViewById(R.id.etReview);

        btnSubmit = findViewById(R.id.btnSubmit);

        btnBack = findViewById(R.id.btnBack);
        ivProduct = findViewById(R.id.ivProduct);
    }

    private void getIntentData() {

        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");

        if (name == null || name.isEmpty()) {
            name = "Produk";
        }

        tvProductName.setText(name);

        if (!TextUtils.isEmpty(image)) {

            Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(ivProduct);

        } else {

            ivProduct.setImageResource(R.drawable.img_placeholder);
        }
    }

    private void setupRating() {

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {

            String text;

            switch ((int) rating) {

                case 1:
                    text = "Sangat Buruk";
                    break;

                case 2:
                    text = "Buruk";
                    break;

                case 3:
                    text = "Cukup";
                    break;

                case 4:
                    text = "Bagus";
                    break;

                case 5:
                    text = "Sangat Bagus";
                    break;

                default:
                    text = "";
                    break;
            }

            tvRatingText.setText(text);
        });
    }

    private void setupButton() {

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {

            float rating = ratingBar.getRating();

            String reviewText = etReview.getText().toString().trim();

            if (rating == 0) {

                Toast.makeText(this,
                        "Berikan rating terlebih dahulu",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            if (reviewText.isEmpty()) {

                Toast.makeText(this,
                        "Tulis ulasan terlebih dahulu",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            String uid = FirebaseAuth.getInstance().getUid();

            if (uid == null) {

                Toast.makeText(this,
                        "User belum login",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            String reviewId = FirebaseDatabase.getInstance()
                    .getReference("reviews")
                    .child(uid)
                    .push()
                    .getKey();

            ReviewModel model = new ReviewModel(
                    name,
                    image,
                    reviewText,
                    (int) rating,
                    System.currentTimeMillis()
            );

            FirebaseDatabase.getInstance()
                    .getReference("reviews")
                    .child(uid)
                    .child(reviewId)
                    .setValue(model)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(this,
                                "Ulasan berhasil dikirim 🎉",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    });
        });
    }
}
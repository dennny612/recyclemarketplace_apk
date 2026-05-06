package com.example.recyclemarketplace.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclemarketplace.R;
import com.example.recyclemarketplace.ReviewActivity;
import com.example.recyclemarketplace.model.ReviewModel;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<ReviewModel> list;

    public ReviewAdapter(Context context, List<ReviewModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReviewModel item = list.get(position);

        // 🔥 SAFE DATA
        final String name = (item.getProductName() != null && !item.getProductName().isEmpty())
                ? item.getProductName()
                : "Produk";

        final String image = (item.getImageUrl() != null)
                ? item.getImageUrl()
                : "";

        final String reviewText = (item.getReviewText() != null && !item.getReviewText().isEmpty())
                ? item.getReviewText()
                : "Belum ada ulasan";

        // 🔥 SET DATA
        holder.tvName.setText(name);
        holder.tvReview.setText(reviewText);

        // 🔥 LOAD IMAGE
        Glide.with(holder.itemView.getContext())
                .load(image)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.ivProduct);

        // 🔥 CLICK ITEM
        holder.itemView.setOnClickListener(v -> openReviewPage(item));

        // 🔁 BELI LAGI
        holder.btnBuyAgain.setOnClickListener(v ->
                Toast.makeText(context,
                        "Beli lagi " + name,
                        Toast.LENGTH_SHORT).show()
        );

        // ✍️ TULIS ULASAN
        holder.btnReview.setOnClickListener(v -> openReviewPage(item));
    }

    // 🔥 PINDAH KE REVIEW ACTIVITY
    private void openReviewPage(ReviewModel item) {

        Intent intent = new Intent(context, ReviewActivity.class);

        intent.putExtra("name", item.getProductName());
        intent.putExtra("image", item.getImageUrl());

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProduct;
        TextView tvName, tvReview; // 🔥 TAMBAHAN
        Button btnBuyAgain, btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProduct   = itemView.findViewById(R.id.ivProduct);
            tvName      = itemView.findViewById(R.id.tvName);
            tvReview    = itemView.findViewById(R.id.tvReview); // 🔥 WAJIB ADA
            btnBuyAgain = itemView.findViewById(R.id.btnBuyAgain);
            btnReview   = itemView.findViewById(R.id.btnReview);
        }
    }
}
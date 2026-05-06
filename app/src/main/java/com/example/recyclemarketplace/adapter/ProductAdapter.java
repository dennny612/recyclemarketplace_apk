package com.example.recyclemarketplace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclemarketplace.R;
import com.example.recyclemarketplace.model.ProductModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(ProductModel product, int position);
        void onWishlistClick(ProductModel product, int position);
    }

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel product = productList.get(position);

        // 🔥 AMAN NULL
        String nama = product.getNama() != null ? product.getNama() : "Produk";
        int harga = product.getHarga();

        holder.tvProductName.setText(nama);

        // 🔥 FORMAT RUPIAH
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvProductPrice.setText("Rp " + nf.format(harga));

        // 🔥 RATING AMAN
        holder.tvRating.setText(String.valueOf(product.getRating()));
        holder.tvReviews.setText("(" + product.getReviewCount() + " review)");

        // 🔥 GAMBAR DARI CLOUDINARY / URL
        String imageUrl = product.getImgUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.img_placeholder);
        }

        // 🔥 CLICK ITEM
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (listener != null && pos != RecyclerView.NO_POSITION) {
                listener.onProductClick(productList.get(pos), pos);
            }
        });

        // 🔥 CLICK WISHLIST
        holder.btnWishlist.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (listener != null && pos != RecyclerView.NO_POSITION) {
                listener.onWishlistClick(productList.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // 🔥 OPTIONAL (UNTUK UPDATE DATA DINAMIS)
    public void updateData(List<ProductModel> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProductImage;
        ImageButton btnWishlist;
        TextView tvProductName, tvProductPrice, tvRating, tvReviews;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
        }
    }
}
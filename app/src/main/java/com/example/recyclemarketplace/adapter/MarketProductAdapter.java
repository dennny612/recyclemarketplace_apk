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
import com.example.recyclemarketplace.model.MarketProductModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MarketProductAdapter extends RecyclerView.Adapter<MarketProductAdapter.ViewHolder> {

    private Context context;
    private List<MarketProductModel> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onProductClick(MarketProductModel product, int position);
        void onWishlistClick(MarketProductModel product, int position);
    }

    public MarketProductAdapter(Context context, List<MarketProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<MarketProductModel> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_market_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketProductModel product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvProductPrice.setText("Rp " + nf.format(product.getPrice()));

        // 🔥 FIX GAMBAR (SUPPORT CLOUDINARY + DRAWABLE)
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(holder.ivProductImage);
        } else if (product.getImageResId() != 0) {
            holder.ivProductImage.setImageResource(product.getImageResId());
        } else {
            holder.ivProductImage.setImageResource(R.drawable.img_placeholder);
        }

        // CLICK ITEM
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (listener != null && pos != RecyclerView.NO_POSITION) {
                listener.onProductClick(productList.get(pos), pos);
            }
        });

        // CLICK WISHLIST
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProductImage;
        ImageButton btnWishlist;
        TextView tvProductName, tvProductPrice, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnWishlist = itemView.findViewById(R.id.btnWishlist);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
package com.example.recyclemarketplace.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclemarketplace.R;
import com.example.recyclemarketplace.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartModel> list;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    private CartUpdateListener listener;

    public void setCartUpdateListener(CartUpdateListener listener) {
        this.listener = listener;
    }

    public CartAdapter(Context context, List<CartModel> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        ImageButton btnPlus, btnMinus;
        CheckBox checkBox;
        ImageView ivImage;

        public ViewHolder(View v) {
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvQty = v.findViewById(R.id.tvQty);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
            checkBox = v.findViewById(R.id.checkbox);
            ivImage = v.findViewById(R.id.ivImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        CartModel item = list.get(position);
        if (item == null) return;

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        h.tvName.setText(item.getNama() != null ? item.getNama() : "-");
        h.tvPrice.setText("Rp " + nf.format(item.getHarga()));
        h.tvQty.setText(String.valueOf(item.getQty()));

        // 🔥 FIX CHECKBOX BUG
        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(item.isChecked());

        // 🔥 LOAD IMAGE
        Glide.with(context)
                .load(item.getImgUrl())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(h.ivImage);

        // ➕ TAMBAH QTY
        h.btnPlus.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            item.setQty(item.getQty() + 1);
            updateFirebase(item);

            notifyItemChanged(pos);

            if (listener != null) listener.onCartUpdated();
        });

        // ➖ KURANG / HAPUS
        h.btnMinus.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (item.getQty() > 1) {
                item.setQty(item.getQty() - 1);
                updateFirebase(item);
                notifyItemChanged(pos);
            } else {
                // 🔥 HAPUS DARI FIREBASE JIKA QTY = 1
                deleteFromFirebase(item);

                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, list.size());
            }

            if (listener != null) listener.onCartUpdated();
        });

        // ✅ CHECKBOX
        h.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            updateFirebase(item);

            if (listener != null) listener.onCartUpdated();
        });
    }

    // 🔥 UPDATE FIREBASE
    private void updateFirebase(CartModel item) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (item.getId() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(uid)
                .child(item.getId())
                .setValue(item);
    }

    // 🔥 HAPUS DARI FIREBASE
    private void deleteFromFirebase(CartModel item) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (item.getId() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(uid)
                .child(item.getId())
                .removeValue();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
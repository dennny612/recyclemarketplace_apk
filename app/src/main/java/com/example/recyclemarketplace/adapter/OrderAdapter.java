package com.example.recyclemarketplace.adapter;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.R;
import com.example.recyclemarketplace.model.OrderModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context context;
    List<OrderModel> list;

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderModel order = list.get(position);
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // 🔥 DATA UTAMA
        holder.tvItems.setText(order.getItems());
        holder.tvTotal.setText("Rp " + nf.format(order.getTotal()));

        // 🔥 STATUS
        holder.tvStatus.setText("Status: " + capitalize(order.getStatus()));

        // 🔥 FIX DISINI
        holder.tvPayment.setText("Pembayaran: " + safe(order.getPayment()));
        holder.tvCourier.setText("Pengiriman: " + safe(order.getCourier()));

        // 🔥 WARNA STATUS
        switch (order.getStatus()) {
            case "pending":
                holder.tvStatus.setTextColor(0xFFE65100);
                break;
            case "diproses":
                holder.tvStatus.setTextColor(0xFF1565C0);
                break;
            case "selesai":
                holder.tvStatus.setTextColor(0xFF2E7D32);
                break;
            default:
                holder.tvStatus.setTextColor(0xFF424242);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String capitalize(String text) {
        if (text == null || text.length() == 0) return "-";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String safe(String text) {
        return text != null ? text : "-";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItems, tvTotal, tvStatus, tvPayment, tvCourier;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItems   = itemView.findViewById(R.id.tvItems);
            tvTotal   = itemView.findViewById(R.id.tvTotal);
            tvStatus  = itemView.findViewById(R.id.tvStatus);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvCourier = itemView.findViewById(R.id.tvCourier);
        }
    }
}
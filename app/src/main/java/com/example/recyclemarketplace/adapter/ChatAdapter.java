package com.example.recyclemarketplace.adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.R;
import com.example.recyclemarketplace.model.ChatModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    List<ChatModel> list;

    public ChatAdapter(List<ChatModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMessage, tvTime, tvBadge;

        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvMessage = v.findViewById(R.id.tvMessage);
            tvTime = v.findViewById(R.id.tvTime);
            tvBadge = v.findViewById(R.id.tvBadge);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        ChatModel c = list.get(i);
        h.tvName.setText(c.name);
        h.tvMessage.setText(c.message);
        h.tvTime.setText(c.time);

        if (c.badge > 0) {
            h.tvBadge.setText(String.valueOf(c.badge));
        } else {
            h.tvBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
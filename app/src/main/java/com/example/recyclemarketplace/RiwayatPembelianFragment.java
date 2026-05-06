package com.example.recyclemarketplace;

import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.adapter.OrderAdapter;
import com.example.recyclemarketplace.model.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiwayatPembelianFragment extends Fragment {

    private RecyclerView rvOrders;
    private LinearLayout layoutEmpty;

    private OrderAdapter adapter;
    private List<OrderModel> list;

    private DatabaseReference db;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_riwayat_pembelian, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        list = new ArrayList<>();
        adapter = new OrderAdapter(requireContext(), list);

        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(adapter);

        // 🔥 AMBIL UID USER
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 🔥 refresh setiap buka halaman
    }

    private void loadData() {

        if (uid == null) {
            Toast.makeText(getContext(), "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        db = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(uid);

        // 🔥 AMBIL DATA + URUTKAN BERDASARKAN TIMESTAMP
        db.orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            OrderModel order = data.getValue(OrderModel.class);
                            if (order != null) {
                                list.add(order);
                            }
                        }

                        // 🔥 TERBARU DI ATAS
                        Collections.reverse(list);

                        adapter.notifyDataSetChanged();

                        // 🔥 EMPTY STATE
                        if (list.isEmpty()) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                        } else {
                            layoutEmpty.setVisibility(View.GONE);
                            rvOrders.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Gagal load riwayat", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
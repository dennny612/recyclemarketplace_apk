package com.example.recyclemarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclemarketplace.adapter.ReviewAdapter;
import com.example.recyclemarketplace.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<ReviewModel> list;

    private DatabaseReference db;
    private ValueEventListener listener;
    private String uid;

    public ReviewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review, container, false);

        recyclerView = view.findViewById(R.id.rvReview);

        list = new ArrayList<>();
        adapter = new ReviewAdapter(requireContext(), list);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        loadData();

        return view;
    }

    private void loadData() {

        if (uid == null) {
            Toast.makeText(getContext(), "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        db = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(uid);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    ReviewModel review = data.getValue(ReviewModel.class);

                    if (review != null) {
                        list.add(review);
                    }
                }

                // 🔥 URUTKAN TERBARU DI ATAS
                Collections.sort(list, (a, b) ->
                        Long.compare(b.getTimestamp(), a.getTimestamp()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Gagal load review", Toast.LENGTH_SHORT).show();
            }
        };

        db.addValueEventListener(listener);
    }

    // 🔥 PENTING: HAPUS LISTENER BIAR GA MEMORY LEAK
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (db != null && listener != null) {
            db.removeEventListener(listener);
        }
    }
}
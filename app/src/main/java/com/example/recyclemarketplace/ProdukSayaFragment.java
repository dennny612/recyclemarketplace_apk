package com.example.recyclemarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recyclemarketplace.R;

public class ProdukSayaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_produk_saya, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnListProduct = view.findViewById(R.id.btnListProduct);
        btnListProduct.setOnClickListener(v ->
                        Toast.makeText(getContext(), "Buka halaman tambah produk", Toast.LENGTH_SHORT).show()
                // startActivity(new Intent(getActivity(), AddProductActivity.class));
        );
    }
}
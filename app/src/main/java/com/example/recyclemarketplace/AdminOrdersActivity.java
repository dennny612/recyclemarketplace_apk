package com.example.recyclemarketplace;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminOrdersActivity extends AppCompatActivity {

    TextView tvTotal, tvProcess, tvShipped, tvDone;
    Spinner spStatus;
    Button btnSave;

    ArrayList<String> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        // 🔗 INIT VIEW
        tvTotal   = findViewById(R.id.tvTotal);
        tvProcess = findViewById(R.id.tvProcess);
        tvShipped = findViewById(R.id.tvShipped);
        tvDone    = findViewById(R.id.tvDone);

        spStatus  = findViewById(R.id.spStatus);
        btnSave   = findViewById(R.id.btnSave);

        // 🔥 DATA DUMMY
        tvTotal.setText("10");
        tvProcess.setText("4");
        tvShipped.setText("3");
        tvDone.setText("3");

        // 🔥 LIST STATUS
        statusList = new ArrayList<>();
        statusList.add("Diproses");
        statusList.add("Dikirim");
        statusList.add("Selesai");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);

        // 🔥 DEFAULT PILIHAN
        spStatus.setSelection(0);

        // 🔥 SAVE BUTTON
        btnSave.setOnClickListener(v -> {
            String selectedStatus = spStatus.getSelectedItem().toString();

            // 🔁 SIMULASI UPDATE
            updateStats(selectedStatus);

            Toast.makeText(this,
                    "Status berhasil diupdate ke: " + selectedStatus,
                    Toast.LENGTH_SHORT).show();
        });
    }

    // 🔥 UPDATE ANGKA STATISTIK (SIMULASI)
    private void updateStats(String status) {

        int process = Integer.parseInt(tvProcess.getText().toString());
        int shipped = Integer.parseInt(tvShipped.getText().toString());
        int done    = Integer.parseInt(tvDone.getText().toString());

        switch (status) {
            case "Diproses":
                process++;
                break;

            case "Dikirim":
                shipped++;
                break;

            case "Selesai":
                done++;
                break;
        }

        tvProcess.setText(String.valueOf(process));
        tvShipped.setText(String.valueOf(shipped));
        tvDone.setText(String.valueOf(done));
    }
}
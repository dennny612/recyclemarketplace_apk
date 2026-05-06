package com.example.recyclemarketplace;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminReportActivity extends AppCompatActivity {

    TextView txtTotalSales, txtProfit;

    View barJan, barFeb, barMar, barApr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        txtTotalSales = findViewById(R.id.txtTotalSales);
        txtProfit = findViewById(R.id.txtProfit);

        barJan = findViewById(R.id.barJan);
        barFeb = findViewById(R.id.barFeb);
        barMar = findViewById(R.id.barMar);
        barApr = findViewById(R.id.barApr);

        // 🔥 DATA DUMMY
        txtTotalSales.setText("Rp 4.280.000");
        txtProfit.setText("Rp 856.200");

        // 🔥 SET HEIGHT BAR (bisa dari Firebase nanti)
        setBarHeight(barJan, 80);
        setBarHeight(barFeb, 140);
        setBarHeight(barMar, 100);
        setBarHeight(barApr, 180);
    }

    // 🔥 FUNCTION UBAH TINGGI BAR
    private void setBarHeight(View view, int dp) {
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density);

        view.getLayoutParams().height = px;
        view.requestLayout();
    }
}
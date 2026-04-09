package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BestSIPActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private Button btnStartSIP, btnInvestFund1, btnInvestFund2, btnInvestFund3;
    private Button btnCompareFunds, btnSIPCalculator;
    private CardView cardFund1, cardFund2, cardFund3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_sipactivity);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnStartSIP = findViewById(R.id.btnStartSIP);
        btnInvestFund1 = findViewById(R.id.btnInvestFund1);
        btnInvestFund2 = findViewById(R.id.btnInvestFund2);
        btnInvestFund3 = findViewById(R.id.btnInvestFund3);
        btnCompareFunds = findViewById(R.id.btnCompareFunds);
        btnSIPCalculator = findViewById(R.id.btnSIPCalculator);

        cardFund1 = findViewById(R.id.cardFund1);
        cardFund2 = findViewById(R.id.cardFund2);
        cardFund3 = findViewById(R.id.cardFund3);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);
        btnStartSIP.setOnClickListener(this);
        btnInvestFund1.setOnClickListener(this);
        btnInvestFund2.setOnClickListener(this);
        btnInvestFund3.setOnClickListener(this);
        btnCompareFunds.setOnClickListener(this);
        btnSIPCalculator.setOnClickListener(this);

        cardFund1.setOnClickListener(this);
        cardFund2.setOnClickListener(this);
        cardFund3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnStartSIP) {
            Toast.makeText(this, "Start SIP Process", Toast.LENGTH_SHORT).show();
            // Start SIP setup process
        } else if (id == R.id.btnInvestFund1 || id == R.id.cardFund1) {
            Toast.makeText(this, "Invest in HDFC Mid-Cap Opportunities Fund", Toast.LENGTH_SHORT).show();
            // Open fund details and investment process
        } else if (id == R.id.btnInvestFund2 || id == R.id.cardFund2) {
            Toast.makeText(this, "Invest in Axis Bluechip Fund", Toast.LENGTH_SHORT).show();
            // Open fund details and investment process
        } else if (id == R.id.btnInvestFund3 || id == R.id.cardFund3) {
            Toast.makeText(this, "Invest in Mirae Asset Emerging Bluechip", Toast.LENGTH_SHORT).show();
            // Open fund details and investment process
        } else if (id == R.id.btnCompareFunds) {
            Toast.makeText(this, "Compare Funds", Toast.LENGTH_SHORT).show();
            // Open fund comparison tool
        } else if (id == R.id.btnSIPCalculator) {
            Toast.makeText(this, "SIP Calculator", Toast.LENGTH_SHORT).show();
            // Open SIP calculator
        }
    }
}
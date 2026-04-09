package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MutualFundsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private EditText etSearch;
    private Button btnAllFunds, btnEquity, btnDebt, btnHybrid, btnIndex;
    private Button btnInvestFund1, btnInvestFund2, btnInvestFund3, btnInvestRecommended1;
    private Button btnViewAllTopRated, btnViewAllRecommended;
    private CardView cardFund1, cardFund2, cardFund3, cardRecommendedFund1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_funds);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Category buttons
        btnAllFunds = findViewById(R.id.btnAllFunds);
        btnEquity = findViewById(R.id.btnEquity);
        btnDebt = findViewById(R.id.btnDebt);
        btnHybrid = findViewById(R.id.btnHybrid);
        btnIndex = findViewById(R.id.btnIndex);

        // Fund cards and buttons
        cardFund1 = findViewById(R.id.cardFund1);
        cardFund2 = findViewById(R.id.cardFund2);
        cardFund3 = findViewById(R.id.cardFund3);
        cardRecommendedFund1 = findViewById(R.id.cardRecommendedFund1);

        btnInvestFund1 = findViewById(R.id.btnInvestFund1);
        btnInvestFund2 = findViewById(R.id.btnInvestFund2);
        btnInvestFund3 = findViewById(R.id.btnInvestFund3);
        btnInvestRecommended1 = findViewById(R.id.btnInvestRecommended1);

        // View all buttons
        btnViewAllTopRated = findViewById(R.id.btnViewAllTopRated);
        btnViewAllRecommended = findViewById(R.id.btnViewAllRecommended);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Category buttons
        btnAllFunds.setOnClickListener(this);
        btnEquity.setOnClickListener(this);
        btnDebt.setOnClickListener(this);
        btnHybrid.setOnClickListener(this);
        btnIndex.setOnClickListener(this);

        // Fund cards and buttons
        cardFund1.setOnClickListener(this);
        cardFund2.setOnClickListener(this);
        cardFund3.setOnClickListener(this);
        cardRecommendedFund1.setOnClickListener(this);

        btnInvestFund1.setOnClickListener(this);
        btnInvestFund2.setOnClickListener(this);
        btnInvestFund3.setOnClickListener(this);
        btnInvestRecommended1.setOnClickListener(this);

        // View all buttons
        btnViewAllTopRated.setOnClickListener(this);
        btnViewAllRecommended.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Category buttons
        else if (id == R.id.btnAllFunds) {
            updateCategorySelection(btnAllFunds);
            Toast.makeText(this, "All Funds Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnEquity) {
            updateCategorySelection(btnEquity);
            Toast.makeText(this, "Equity Funds Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnDebt) {
            updateCategorySelection(btnDebt);
            Toast.makeText(this, "Debt Funds Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnHybrid) {
            updateCategorySelection(btnHybrid);
            Toast.makeText(this, "Hybrid Funds Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnIndex) {
            updateCategorySelection(btnIndex);
            Toast.makeText(this, "Index Funds Selected", Toast.LENGTH_SHORT).show();
        }
        // Fund cards and buttons
        else if (id == R.id.cardFund1 || id == R.id.btnInvestFund1) {
            Toast.makeText(this, "SBI Blue Chip Fund Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.cardFund2 || id == R.id.btnInvestFund2) {
            Toast.makeText(this, "Kotak Standard Multicap Fund Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.cardFund3 || id == R.id.btnInvestFund3) {
            Toast.makeText(this, "ICICI Prudential Value Discovery Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.cardRecommendedFund1 || id == R.id.btnInvestRecommended1) {
            Toast.makeText(this, "Aditya Birla Sun Life Tax Relief 96 Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        }
        // View all buttons
        else if (id == R.id.btnViewAllTopRated) {
            Toast.makeText(this, "View All Top Rated Funds", Toast.LENGTH_SHORT).show();
            // Open all top rated funds
        } else if (id == R.id.btnViewAllRecommended) {
            Toast.makeText(this, "View All Recommended Funds", Toast.LENGTH_SHORT).show();
            // Open all recommended funds
        }
    }

    private void updateCategorySelection(Button selectedButton) {
        // Reset all buttons to default state
        btnAllFunds.setBackgroundTintList(getColorStateList(android.R.color.white));
        btnAllFunds.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        btnEquity.setBackgroundTintList(getColorStateList(android.R.color.white));
        btnEquity.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        btnDebt.setBackgroundTintList(getColorStateList(android.R.color.white));
        btnDebt.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        btnHybrid.setBackgroundTintList(getColorStateList(android.R.color.white));
        btnHybrid.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        btnIndex.setBackgroundTintList(getColorStateList(android.R.color.white));
        btnIndex.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        // Set selected button to active state
        selectedButton.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
        selectedButton.setTextColor(getResources().getColor(android.R.color.white));
    }
}
package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LargeCapFundsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private Button btnSortByReturns, btnFilterRating, btnFilterRisk;
    private Button btnInvestFund1, btnInvestFund2, btnInvestFund3;
    private Button btnCompareFunds, btnViewAllFunds;
    private CardView cardFund1, cardFund2, cardFund3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_cap_funds);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Filter buttons
        btnSortByReturns = findViewById(R.id.btnSortByReturns);
        btnFilterRating = findViewById(R.id.btnFilterRating);
        btnFilterRisk = findViewById(R.id.btnFilterRisk);

        // Fund cards
        cardFund1 = findViewById(R.id.cardFund1);
        cardFund2 = findViewById(R.id.cardFund2);
        cardFund3 = findViewById(R.id.cardFund3);

        // Invest buttons
        btnInvestFund1 = findViewById(R.id.btnInvestFund1);
        btnInvestFund2 = findViewById(R.id.btnInvestFund2);
        btnInvestFund3 = findViewById(R.id.btnInvestFund3);

        // Action buttons
        btnCompareFunds = findViewById(R.id.btnCompareFunds);
        btnViewAllFunds = findViewById(R.id.btnViewAllFunds);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Filter buttons
        btnSortByReturns.setOnClickListener(this);
        btnFilterRating.setOnClickListener(this);
        btnFilterRisk.setOnClickListener(this);

        // Fund cards
        cardFund1.setOnClickListener(this);
        cardFund2.setOnClickListener(this);
        cardFund3.setOnClickListener(this);

        // Invest buttons
        btnInvestFund1.setOnClickListener(this);
        btnInvestFund2.setOnClickListener(this);
        btnInvestFund3.setOnClickListener(this);

        // Action buttons
        btnCompareFunds.setOnClickListener(this);
        btnViewAllFunds.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Filter buttons
        else if (id == R.id.btnSortByReturns) {
            Toast.makeText(this, "Sorting by Returns", Toast.LENGTH_SHORT).show();
            // Toggle sort order
            if (btnSortByReturns.getText().toString().contains("↓")) {
                btnSortByReturns.setText("Sort by Returns ↑");
            } else {
                btnSortByReturns.setText("Sort by Returns ↓");
            }
            // Implement sorting logic
        } else if (id == R.id.btnFilterRating) {
            Toast.makeText(this, "Filtering by Rating", Toast.LENGTH_SHORT).show();
            // Show rating filter options
        } else if (id == R.id.btnFilterRisk) {
            Toast.makeText(this, "Filtering by Risk", Toast.LENGTH_SHORT).show();
            // Show risk filter options
        }
        // Fund cards and invest buttons
        else if (id == R.id.cardFund1) {
            Toast.makeText(this, "HDFC Top 100 Fund Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.btnInvestFund1) {
            Toast.makeText(this, "Invest in HDFC Top 100 Fund", Toast.LENGTH_SHORT).show();
            // Open investment flow
        } else if (id == R.id.cardFund2) {
            Toast.makeText(this, "Nippon India Large Cap Fund Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.btnInvestFund2) {
            Toast.makeText(this, "Invest in Nippon India Large Cap Fund", Toast.LENGTH_SHORT).show();
            // Open investment flow
        } else if (id == R.id.cardFund3) {
            Toast.makeText(this, "Axis Bluechip Fund Details", Toast.LENGTH_SHORT).show();
            // Open fund details
        } else if (id == R.id.btnInvestFund3) {
            Toast.makeText(this, "Invest in Axis Bluechip Fund", Toast.LENGTH_SHORT).show();
            // Open investment flow
        }
        // Action buttons
        else if (id == R.id.btnCompareFunds) {
            Toast.makeText(this, "Compare Selected Funds", Toast.LENGTH_SHORT).show();
            // Open fund comparison tool
        } else if (id == R.id.btnViewAllFunds) {
            Toast.makeText(this, "View All Large CAP Funds", Toast.LENGTH_SHORT).show();
            // Show all large cap funds
        }
    }
}
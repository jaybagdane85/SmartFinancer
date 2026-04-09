package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MoneyTakenActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTotalMoneyTaken, tvMonthlyTaken, tvToBeReturned;
    private Button btnAddNew, btnViewAll, btnReturnMoney;
    private CardView cardTransaction1, cardTransaction2, cardTransaction3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_taken);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Summary views
        tvTotalMoneyTaken = findViewById(R.id.tvTotalMoneyTaken);
        tvMonthlyTaken = findViewById(R.id.tvMonthlyTaken);
        tvToBeReturned = findViewById(R.id.tvToBeReturned);

        // Buttons
        btnAddNew = findViewById(R.id.btnAddNew);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnReturnMoney = findViewById(R.id.btnReturnMoney);

        // Transaction cards
        cardTransaction1 = findViewById(R.id.cardTransaction1);
        cardTransaction2 = findViewById(R.id.cardTransaction2);
        cardTransaction3 = findViewById(R.id.cardTransaction3);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Buttons
        btnAddNew.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        btnReturnMoney.setOnClickListener(this);

        // Transaction cards
        cardTransaction1.setOnClickListener(this);
        cardTransaction2.setOnClickListener(this);
        cardTransaction3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Buttons
        else if (id == R.id.btnAddNew) {
            showAddNewTransactionDialog();
        } else if (id == R.id.btnViewAll) {
            Toast.makeText(this, "Viewing all transactions", Toast.LENGTH_SHORT).show();
            // Show all transactions
        } else if (id == R.id.btnReturnMoney) {
            Toast.makeText(this, "Return money options", Toast.LENGTH_SHORT).show();
            // Show return money options
        }
        // Transaction cards
        else if (id == R.id.cardTransaction1) {
            Toast.makeText(this, "Transaction details with Priya", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardTransaction2) {
            Toast.makeText(this, "Transaction details with Suresh", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardTransaction3) {
            Toast.makeText(this, "Transaction details with Vikram", Toast.LENGTH_SHORT).show();
            // Show transaction details
        }
    }

    private void showAddNewTransactionDialog() {
        // In a real app, this would show a dialog to add a new transaction
        // For now, we'll just show a toast
        Toast.makeText(this, "Add new money taken transaction", Toast.LENGTH_SHORT).show();
    }
}
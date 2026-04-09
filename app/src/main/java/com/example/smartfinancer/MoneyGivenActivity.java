package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MoneyGivenActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTotalMoneyGiven, tvMonthlyGiven, tvPendingAmount;
    private Button btnAddNew, btnViewAll, btnSendReminders;
    private CardView cardTransaction1, cardTransaction2, cardTransaction3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_given);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Summary views
        tvTotalMoneyGiven = findViewById(R.id.tvTotalMoneyGiven);
        tvMonthlyGiven = findViewById(R.id.tvMonthlyGiven);
        tvPendingAmount = findViewById(R.id.tvPendingAmount);

        // Buttons
        btnAddNew = findViewById(R.id.btnAddNew);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnSendReminders = findViewById(R.id.btnSendReminders);

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
        btnSendReminders.setOnClickListener(this);

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
        } else if (id == R.id.btnSendReminders) {
            Toast.makeText(this, "Sending reminders to all pending contacts", Toast.LENGTH_SHORT).show();
            // Send reminders
        }
        // Transaction cards
        else if (id == R.id.cardTransaction1) {
            Toast.makeText(this, "Transaction details with Rahul", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardTransaction2) {
            Toast.makeText(this, "Transaction details with Amit", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardTransaction3) {
            Toast.makeText(this, "Transaction details with Neha", Toast.LENGTH_SHORT).show();
            // Show transaction details
        }
    }

    private void showAddNewTransactionDialog() {
        // In a real app, this would show a dialog to add a new transaction
        // For now, we'll just show a toast
        Toast.makeText(this, "Add new money given transaction", Toast.LENGTH_SHORT).show();
    }
}
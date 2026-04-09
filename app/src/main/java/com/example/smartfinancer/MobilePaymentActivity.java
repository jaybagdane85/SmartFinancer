package com.example.smartfinancer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MobilePaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private EditText etMobileNumber;
    private Button btnVerify, btnViewAllTransactions;
    private LinearLayout layoutContact1, layoutContact2, layoutContact3, layoutContact4, layoutViewAllContacts;
    private CardView cardTransaction1, cardTransaction2, cardScanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_payment);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        btnVerify = findViewById(R.id.btnVerify);
        btnViewAllTransactions = findViewById(R.id.btnViewAllTransactions);

        // Contact layouts
        layoutContact1 = findViewById(R.id.layoutContact1);
        layoutContact2 = findViewById(R.id.layoutContact2);
        layoutContact3 = findViewById(R.id.layoutContact3);
        layoutContact4 = findViewById(R.id.layoutContact4);
        layoutViewAllContacts = findViewById(R.id.layoutViewAllContacts);

        // Transaction cards
        cardTransaction1 = findViewById(R.id.cardTransaction1);
        cardTransaction2 = findViewById(R.id.cardTransaction2);
        cardScanQR = findViewById(R.id.cardScanQR);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        btnViewAllTransactions.setOnClickListener(this);

        // Contact layouts
        layoutContact1.setOnClickListener(this);
        layoutContact2.setOnClickListener(this);
        layoutContact3.setOnClickListener(this);
        layoutContact4.setOnClickListener(this);
        layoutViewAllContacts.setOnClickListener(this);

        // Transaction cards
        cardTransaction1.setOnClickListener(this);
        cardTransaction2.setOnClickListener(this);
        cardScanQR.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnVerify) {
            verifyMobileNumber();
        } else if (id == R.id.btnViewAllTransactions) {
            Toast.makeText(this, "Viewing all transactions", Toast.LENGTH_SHORT).show();
            // Open transactions history screen
        }
        // Contact layouts
        else if (id == R.id.layoutContact1) {
            etMobileNumber.setText("9876543210");
            Toast.makeText(this, "Selected Rahul", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.layoutContact2) {
            etMobileNumber.setText("9876543211");
            Toast.makeText(this, "Selected Priya", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.layoutContact3) {
            etMobileNumber.setText("9876543212");
            Toast.makeText(this, "Selected Amit", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.layoutContact4) {
            etMobileNumber.setText("9876543213");
            Toast.makeText(this, "Selected Neha", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.layoutViewAllContacts) {
            Toast.makeText(this, "Viewing all contacts", Toast.LENGTH_SHORT).show();
            // Open contacts screen
        }
        // Transaction cards
        else if (id == R.id.cardTransaction1) {
            Toast.makeText(this, "Transaction details with Rahul", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardTransaction2) {
            Toast.makeText(this, "Transaction details with Amit", Toast.LENGTH_SHORT).show();
            // Show transaction details
        } else if (id == R.id.cardScanQR) {
            // Open QR scanner activity
            startActivity(new Intent(this, QRScannerActivity.class));
        }
    }

    private void verifyMobileNumber() {
        String mobileNumber = etMobileNumber.getText().toString().trim();

        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mobileNumber.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, this would verify the mobile number with a backend service
        // For now, we'll just show a toast and open the payment amount screen
        Toast.makeText(this, "Verified mobile number: " + mobileNumber, Toast.LENGTH_SHORT).show();

        // Show payment amount dialog
        showPaymentAmountDialog(mobileNumber);
    }

    private void showPaymentAmountDialog(String mobileNumber) {
        // In a real app, this would show a dialog to enter the payment amount
        // For now, we'll just show a toast
        Toast.makeText(this, "Enter payment amount for " + mobileNumber, Toast.LENGTH_SHORT).show();
    }
}
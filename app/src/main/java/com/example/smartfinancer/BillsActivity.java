package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BillsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private EditText etSearch;
    private LinearLayout layoutElectricity, layoutWater, layoutGas, layoutBroadband;
    private CardView cardBill1, cardBill2, cardPayment1, cardPayment2;
    private Button btnPayBill1, btnPayBill2, btnViewAllPayments, btnAddNewBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Category layouts
        layoutElectricity = findViewById(R.id.layoutElectricity);
        layoutWater = findViewById(R.id.layoutWater);
        layoutGas = findViewById(R.id.layoutGas);
        layoutBroadband = findViewById(R.id.layoutBroadband);

        // Bill cards
        cardBill1 = findViewById(R.id.cardBill1);
        cardBill2 = findViewById(R.id.cardBill2);
        cardPayment1 = findViewById(R.id.cardPayment1);
        cardPayment2 = findViewById(R.id.cardPayment2);

        // Buttons
        btnPayBill1 = findViewById(R.id.btnPayBill1);
        btnPayBill2 = findViewById(R.id.btnPayBill2);
        btnViewAllPayments = findViewById(R.id.btnViewAllPayments);
        btnAddNewBill = findViewById(R.id.btnAddNewBill);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Category layouts
        layoutElectricity.setOnClickListener(this);
        layoutWater.setOnClickListener(this);
        layoutGas.setOnClickListener(this);
        layoutBroadband.setOnClickListener(this);

        // Bill cards
        cardBill1.setOnClickListener(this);
        cardBill2.setOnClickListener(this);
        cardPayment1.setOnClickListener(this);
        cardPayment2.setOnClickListener(this);

        // Buttons
        btnPayBill1.setOnClickListener(this);
        btnPayBill2.setOnClickListener(this);
        btnViewAllPayments.setOnClickListener(this);
        btnAddNewBill.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Category layouts
        else if (id == R.id.layoutElectricity) {
            Toast.makeText(this, "Electricity bills", Toast.LENGTH_SHORT).show();
            // Show electricity bill providers
        } else if (id == R.id.layoutWater) {
            Toast.makeText(this, "Water bills", Toast.LENGTH_SHORT).show();
            // Show water bill providers
        } else if (id == R.id.layoutGas) {
            Toast.makeText(this, "Gas bills", Toast.LENGTH_SHORT).show();
            // Show gas bill providers
        } else if (id == R.id.layoutBroadband) {
            Toast.makeText(this, "Broadband bills", Toast.LENGTH_SHORT).show();
            // Show broadband bill providers
        }
        // Bill cards
        else if (id == R.id.cardBill1 || id == R.id.btnPayBill1) {
            Toast.makeText(this, "Paying electricity bill of ₹1,250", Toast.LENGTH_SHORT).show();
            // Show payment options for electricity bill
        } else if (id == R.id.cardBill2 || id == R.id.btnPayBill2) {
            Toast.makeText(this, "Paying broadband bill of ₹999", Toast.LENGTH_SHORT).show();
            // Show payment options for broadband bill
        } else if (id == R.id.cardPayment1) {
            Toast.makeText(this, "Water bill payment details", Toast.LENGTH_SHORT).show();
            // Show water bill payment details
        } else if (id == R.id.cardPayment2) {
            Toast.makeText(this, "Electricity bill payment details", Toast.LENGTH_SHORT).show();
            // Show electricity bill payment details
        }
        // Buttons
        else if (id == R.id.btnViewAllPayments) {
            Toast.makeText(this, "Viewing all bill payments", Toast.LENGTH_SHORT).show();
            // Show all bill payments history
        } else if (id == R.id.btnAddNewBill) {
            Toast.makeText(this, "Adding new bill", Toast.LENGTH_SHORT).show();
            // Show add new bill screen
        }
    }
}
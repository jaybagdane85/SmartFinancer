package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RentsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTotalRent, tvNextDueDate, tvStatus;
    private Button btnAddNew, btnPayRent1, btnPayRent2, btnViewAll, btnSetReminders;
    private CardView cardProperty1, cardProperty2, cardPayment1, cardPayment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rents);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Summary views
        tvTotalRent = findViewById(R.id.tvTotalRent);
        tvNextDueDate = findViewById(R.id.tvNextDueDate);
        tvStatus = findViewById(R.id.tvStatus);

        // Buttons
        btnAddNew = findViewById(R.id.btnAddNew);
        btnPayRent1 = findViewById(R.id.btnPayRent1);
        btnPayRent2 = findViewById(R.id.btnPayRent2);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnSetReminders = findViewById(R.id.btnSetReminders);

        // Cards
        cardProperty1 = findViewById(R.id.cardProperty1);
        cardProperty2 = findViewById(R.id.cardProperty2);
        cardPayment1 = findViewById(R.id.cardPayment1);
        cardPayment2 = findViewById(R.id.cardPayment2);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Buttons
        btnAddNew.setOnClickListener(this);
        btnPayRent1.setOnClickListener(this);
        btnPayRent2.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        btnSetReminders.setOnClickListener(this);

        // Cards
        cardProperty1.setOnClickListener(this);
        cardProperty2.setOnClickListener(this);
        cardPayment1.setOnClickListener(this);
        cardPayment2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Buttons
        else if (id == R.id.btnAddNew) {
            Toast.makeText(this, "Add new rent property", Toast.LENGTH_SHORT).show();
            // Show add new rent property dialog
        } else if (id == R.id.btnPayRent1) {
            Toast.makeText(this, "Pay home rent of ₹12,000", Toast.LENGTH_SHORT).show();
            // Show payment options for home rent
        } else if (id == R.id.btnPayRent2) {
            Toast.makeText(this, "Pay office rent of ₹3,000", Toast.LENGTH_SHORT).show();
            // Show payment options for office rent
        } else if (id == R.id.btnViewAll) {
            Toast.makeText(this, "View all rent payments", Toast.LENGTH_SHORT).show();
            // Show all rent payments history
        } else if (id == R.id.btnSetReminders) {
            Toast.makeText(this, "Set rent payment reminders", Toast.LENGTH_SHORT).show();
            // Show reminder settings
        }
        // Cards
        else if (id == R.id.cardProperty1) {
            Toast.makeText(this, "Home rent details", Toast.LENGTH_SHORT).show();
            // Show home rent details
        } else if (id == R.id.cardProperty2) {
            Toast.makeText(this, "Office rent details", Toast.LENGTH_SHORT).show();
            // Show office rent details
        } else if (id == R.id.cardPayment1) {
            Toast.makeText(this, "Home rent payment details", Toast.LENGTH_SHORT).show();
            // Show home rent payment details
        } else if (id == R.id.cardPayment2) {
            Toast.makeText(this, "Office rent payment details", Toast.LENGTH_SHORT).show();
            // Show office rent payment details
        }
    }
}
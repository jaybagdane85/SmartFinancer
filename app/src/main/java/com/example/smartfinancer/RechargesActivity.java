package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.tabs.TabLayout;

public class RechargesActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TabLayout tabLayout;
    private EditText etMobileNumber, etAmount;
    private Spinner spinnerOperator, spinnerCircle;
    private Button btnViewPlans, btnProceed, btnViewAllRecharges, btnApplyOffer1;
    private CardView cardRecharge1, cardRecharge2, cardOffer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharges);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize spinners
        initSpinners();

        // Setup tab layout
        setupTabLayout();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tabLayout = findViewById(R.id.tabLayout);

        // Input fields
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAmount = findViewById(R.id.etAmount);
        spinnerOperator = findViewById(R.id.spinnerOperator);
        spinnerCircle = findViewById(R.id.spinnerCircle);

        // Buttons
        btnViewPlans = findViewById(R.id.btnViewPlans);
        btnProceed = findViewById(R.id.btnProceed);
        btnViewAllRecharges = findViewById(R.id.btnViewAllRecharges);
        btnApplyOffer1 = findViewById(R.id.btnApplyOffer1);

        // Cards
        cardRecharge1 = findViewById(R.id.cardRecharge1);
        cardRecharge2 = findViewById(R.id.cardRecharge2);
        cardOffer1 = findViewById(R.id.cardOffer1);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Buttons
        btnViewPlans.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
        btnViewAllRecharges.setOnClickListener(this);
        btnApplyOffer1.setOnClickListener(this);

        // Cards
        cardRecharge1.setOnClickListener(this);
        cardRecharge2.setOnClickListener(this);
        cardOffer1.setOnClickListener(this);
    }

    private void initSpinners() {
        // Operator spinner
        ArrayAdapter<CharSequence> operatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.operators_array, android.R.layout.simple_spinner_item);
        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperator.setAdapter(operatorAdapter);

        // Circle spinner
        ArrayAdapter<CharSequence> circleAdapter = ArrayAdapter.createFromResource(this,
                R.array.circles_array, android.R.layout.simple_spinner_item);
        circleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCircle.setAdapter(circleAdapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // Mobile
                        Toast.makeText(RechargesActivity.this, "Mobile Recharge", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // DTH
                        Toast.makeText(RechargesActivity.this, "DTH Recharge", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // FASTag
                        Toast.makeText(RechargesActivity.this, "FASTag Recharge", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Buttons
        else if (id == R.id.btnViewPlans) {
            String operator = spinnerOperator.getSelectedItem().toString();
            Toast.makeText(this, "Viewing plans for " + operator, Toast.LENGTH_SHORT).show();
            // Show recharge plans
        } else if (id == R.id.btnProceed) {
            proceedWithRecharge();
        } else if (id == R.id.btnViewAllRecharges) {
            Toast.makeText(this, "Viewing all recharge history", Toast.LENGTH_SHORT).show();
            // Show all recharge history
        } else if (id == R.id.btnApplyOffer1) {
            Toast.makeText(this, "Offer applied: 10% cashback", Toast.LENGTH_SHORT).show();
            // Apply offer
        }
        // Cards
        else if (id == R.id.cardRecharge1) {
            Toast.makeText(this, "Mobile recharge details", Toast.LENGTH_SHORT).show();
            // Show mobile recharge details
        } else if (id == R.id.cardRecharge2) {
            Toast.makeText(this, "DTH recharge details", Toast.LENGTH_SHORT).show();
            // Show DTH recharge details
        } else if (id == R.id.cardOffer1) {
            Toast.makeText(this, "Offer details: 10% cashback", Toast.LENGTH_SHORT).show();
            // Show offer details
        }
    }

    private void proceedWithRecharge() {
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (mobileNumber.isEmpty()) {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mobileNumber.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        String operator = spinnerOperator.getSelectedItem().toString();
        String circle = spinnerCircle.getSelectedItem().toString();

        Toast.makeText(this, "Proceeding with recharge of ₹" + amount +
                        " for " + mobileNumber + " (" + operator + ", " + circle + ")",
                Toast.LENGTH_SHORT).show();

        // In a real app, this would initiate the recharge process
    }
}
package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.NumberFormat;
import java.util.Locale;

public class TermLifeActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnBack;
    private EditText etAge;
    private RadioGroup radioGroupGender, radioGroupSmoker;
    private RadioButton radioButtonMale, radioButtonFemale, radioButtonNonSmoker, radioButtonSmoker;
    private SeekBar seekBarCoverage, seekBarTerm;
    private TextView tvCoverageAmount, tvPolicyTerm, tvMonthlyPremium;
    private Button btnCalculatePremium, btnGetQuotes, btnViewProvider1, btnViewProvider2, btnCompareAll;
    private CardView cardProvider1, cardProvider2;

    // Constants for premium calculation
    private static final int MIN_COVERAGE = 5000000; // 50L
    private static final int MAX_COVERAGE = 50000000; // 5Cr
    private static final int MIN_TERM = 10;
    private static final int MAX_TERM = 40;

    // Base premium rates per 1 Crore coverage (annual)
    private static final double BASE_PREMIUM_RATE = 10000; // ₹10,000 per year for 1Cr coverage for a 30-year-old non-smoking male

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_life);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize premium calculator
        initPremiumCalculator();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Premium calculator views
        etAge = findViewById(R.id.etAge);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupSmoker = findViewById(R.id.radioGroupSmoker);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioButtonNonSmoker = findViewById(R.id.radioButtonNonSmoker);
        radioButtonSmoker = findViewById(R.id.radioButtonSmoker);
        seekBarCoverage = findViewById(R.id.seekBarCoverage);
        seekBarTerm = findViewById(R.id.seekBarTerm);
        tvCoverageAmount = findViewById(R.id.tvCoverageAmount);
        tvPolicyTerm = findViewById(R.id.tvPolicyTerm);
        tvMonthlyPremium = findViewById(R.id.tvMonthlyPremium);
        btnCalculatePremium = findViewById(R.id.btnCalculatePremium);
        btnGetQuotes = findViewById(R.id.btnGetQuotes);

        // Provider views
        cardProvider1 = findViewById(R.id.cardProvider1);
        cardProvider2 = findViewById(R.id.cardProvider2);
        btnViewProvider1 = findViewById(R.id.btnViewProvider1);
        btnViewProvider2 = findViewById(R.id.btnViewProvider2);
        btnCompareAll = findViewById(R.id.btnCompareAll);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Premium calculator views
        seekBarCoverage.setOnSeekBarChangeListener(this);
        seekBarTerm.setOnSeekBarChangeListener(this);
        btnCalculatePremium.setOnClickListener(this);
        btnGetQuotes.setOnClickListener(this);

        // Provider views
        cardProvider1.setOnClickListener(this);
        cardProvider2.setOnClickListener(this);
        btnViewProvider1.setOnClickListener(this);
        btnViewProvider2.setOnClickListener(this);
        btnCompareAll.setOnClickListener(this);
    }

    private void initPremiumCalculator() {
        // Set initial values
        seekBarCoverage.setProgress(50); // 50% of the range
        seekBarTerm.setProgress(20); // 30 years

        // Update UI with initial values
        updateCoverageUI(getCoverageFromProgress(seekBarCoverage.getProgress()));
        updateTermUI(getTermFromProgress(seekBarTerm.getProgress()));

        // Calculate and update premium
        calculatePremium();
    }

    private int getCoverageFromProgress(int progress) {
        // Convert progress (0-100) to coverage amount
        return MIN_COVERAGE + (progress * (MAX_COVERAGE - MIN_COVERAGE) / 100);
    }

    private int getTermFromProgress(int progress) {
        // Convert progress (0-30) to policy term
        return MIN_TERM + progress;
    }

    private void updateCoverageUI(int coverage) {
        // Format coverage amount
        String formattedCoverage;
        if (coverage >= 10000000) {
            formattedCoverage = "₹" + (coverage / 10000000) + " Crore";
        } else {
            formattedCoverage = "₹" + (coverage / 100000) + " Lakh";
        }
        tvCoverageAmount.setText(formattedCoverage);
    }

    private void updateTermUI(int term) {
        tvPolicyTerm.setText(term + " years");
    }

    private void calculatePremium() {
        try {
            // Get user inputs
            int age = Integer.parseInt(etAge.getText().toString());
            boolean isMale = radioButtonMale.isChecked();
            boolean isSmoker = radioButtonSmoker.isChecked();
            int coverage = getCoverageFromProgress(seekBarCoverage.getProgress());
            int term = getTermFromProgress(seekBarTerm.getProgress());

            // Calculate premium based on factors
            double coverageInCrores = coverage / 10000000.0; // Convert to crores

            // Base premium for 1 Crore coverage
            double annualPremium = BASE_PREMIUM_RATE * coverageInCrores;

            // Apply age factor (premium increases with age)
            double ageFactor = 1.0 + ((age - 30) * 0.05); // 5% increase for each year above 30

            // Apply gender factor (females typically get lower premiums)
            double genderFactor = isMale ? 1.0 : 0.9;

            // Apply smoker factor (smokers pay higher premiums)
            double smokerFactor = isSmoker ? 1.5 : 1.0;

            // Apply term factor (longer terms have higher premiums)
            double termFactor = 1.0 + ((term - 20) * 0.02); // 2% increase for each year above 20

            // Calculate final annual premium
            annualPremium = annualPremium * ageFactor * genderFactor * smokerFactor * termFactor;

            // Calculate monthly premium
            double monthlyPremium = annualPremium / 12;

            // Format and update UI
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String formattedMonthlyPremium = formatter.format(monthlyPremium).replace("₹", "₹");
            String formattedAnnualPremium = formatter.format(annualPremium).replace("₹", "₹");

            tvMonthlyPremium.setText(formattedMonthlyPremium);

            // Update annual premium text
            TextView tvAnnualPremium = findViewById(R.id.tvMonthlyPremium).getRootView().findViewById(android.R.id.content)
                    .findViewById(R.id.tvMonthlyPremium)
                    .getRootView().findViewWithTag("Annual Premium");
            if (tvAnnualPremium != null) {
                tvAnnualPremium.setText("Annual Premium: " + formattedAnnualPremium);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Premium calculator buttons
        else if (id == R.id.btnCalculatePremium) {
            calculatePremium();
        } else if (id == R.id.btnGetQuotes) {
            Toast.makeText(this, "Getting personalized quotes", Toast.LENGTH_SHORT).show();
            // Start quote request process
        }
        // Provider views and buttons
        else if (id == R.id.cardProvider1 || id == R.id.btnViewProvider1) {
            Toast.makeText(this, "Viewing HDFC Life policy details", Toast.LENGTH_SHORT).show();
            // Show HDFC Life policy details
        } else if (id == R.id.cardProvider2 || id == R.id.btnViewProvider2) {
            Toast.makeText(this, "Viewing ICICI Prudential policy details", Toast.LENGTH_SHORT).show();
            // Show ICICI Prudential policy details
        } else if (id == R.id.btnCompareAll) {
            Toast.makeText(this, "Comparing all insurance providers", Toast.LENGTH_SHORT).show();
            // Show comparison of all providers
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();

        if (id == R.id.seekBarCoverage) {
            int coverage = getCoverageFromProgress(progress);
            updateCoverageUI(coverage);
        } else if (id == R.id.seekBarTerm) {
            int term = getTermFromProgress(progress);
            updateTermUI(term);
        }

        // Recalculate premium whenever any slider changes
        if (fromUser) {
            calculatePremium();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Not needed
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Not needed
    }
}
package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HealthInsuranceActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnBack;
    private RadioGroup radioGroupInsureType;
    private RadioButton radioButtonSelf, radioButtonFamily, radioButtonParents;
    private EditText etAge;
    private Spinner spinnerCity;
    private SeekBar seekBarCoverage;
    private TextView tvCoverageAmount;
    private Button btnFindPlans, btnViewPlan1, btnViewPlan2, btnBuyPlan1, btnBuyPlan2, btnCompareAll;
    private CardView cardPlan1, cardPlan2;

    // Constants for coverage calculation
    private static final int MIN_COVERAGE = 300000; // 3L
    private static final int MAX_COVERAGE = 10000000; // 1Cr

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_insurance);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize city spinner
        initCitySpinner();

        // Initialize coverage slider
        initCoverageSlider();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Plan finder views
        radioGroupInsureType = findViewById(R.id.radioGroupInsureType);
        radioButtonSelf = findViewById(R.id.radioButtonSelf);
        radioButtonFamily = findViewById(R.id.radioButtonFamily);
        radioButtonParents = findViewById(R.id.radioButtonParents);
        etAge = findViewById(R.id.etAge);
        spinnerCity = findViewById(R.id.spinnerCity);
        seekBarCoverage = findViewById(R.id.seekBarCoverage);
        tvCoverageAmount = findViewById(R.id.tvCoverageAmount);
        btnFindPlans = findViewById(R.id.btnFindPlans);

        // Plan cards and buttons
        cardPlan1 = findViewById(R.id.cardPlan1);
        cardPlan2 = findViewById(R.id.cardPlan2);
        btnViewPlan1 = findViewById(R.id.btnViewPlan1);
        btnViewPlan2 = findViewById(R.id.btnViewPlan2);
        btnBuyPlan1 = findViewById(R.id.btnBuyPlan1);
        btnBuyPlan2 = findViewById(R.id.btnBuyPlan2);
        btnCompareAll = findViewById(R.id.btnCompareAll);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Plan finder views
        seekBarCoverage.setOnSeekBarChangeListener(this);
        btnFindPlans.setOnClickListener(this);

        // Plan cards and buttons
        cardPlan1.setOnClickListener(this);
        cardPlan2.setOnClickListener(this);
        btnViewPlan1.setOnClickListener(this);
        btnViewPlan2.setOnClickListener(this);
        btnBuyPlan1.setOnClickListener(this);
        btnBuyPlan2.setOnClickListener(this);
        btnCompareAll.setOnClickListener(this);
    }

    private void initCitySpinner() {
        // Create an ArrayAdapter using a simple spinner layout and city values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCity.setAdapter(adapter);
    }

    private void initCoverageSlider() {
        // Set initial progress
        seekBarCoverage.setProgress(50);

        // Update UI with initial value
        updateCoverageUI(getCoverageFromProgress(seekBarCoverage.getProgress()));
    }

    private int getCoverageFromProgress(int progress) {
        // Convert progress (0-100) to coverage amount
        return MIN_COVERAGE + (progress * (MAX_COVERAGE - MIN_COVERAGE) / 100);
    }

    private void updateCoverageUI(int coverage) {
        // Format coverage amount
        String formattedCoverage;
        if (coverage >= 10000000) {
            formattedCoverage = "₹1 Crore";
        } else if (coverage >= 100000) {
            formattedCoverage = "₹" + (coverage / 100000) + " Lakh";
        } else {
            formattedCoverage = "₹" + coverage;
        }
        tvCoverageAmount.setText(formattedCoverage);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Plan finder buttons
        else if (id == R.id.btnFindPlans) {
            try {
                int age = Integer.parseInt(etAge.getText().toString());
                String insureType = radioButtonSelf.isChecked() ? "Self" :
                        (radioButtonFamily.isChecked() ? "Family" : "Parents");
                String city = spinnerCity.getSelectedItem().toString();
                int coverage = getCoverageFromProgress(seekBarCoverage.getProgress());

                Toast.makeText(this, "Finding plans for " + insureType + ", Age: " + age +
                                ", City: " + city + ", Coverage: " + tvCoverageAmount.getText(),
                        Toast.LENGTH_SHORT).show();

                // In a real app, this would search for matching plans
                // For now, we'll just scroll to the recommended plans section
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            }
        }
        // Plan cards and buttons
        else if (id == R.id.cardPlan1 || id == R.id.btnViewPlan1) {
            Toast.makeText(this, "Viewing Star Health Insurance details", Toast.LENGTH_SHORT).show();
            // Show plan details
        } else if (id == R.id.cardPlan2 || id == R.id.btnViewPlan2) {
            Toast.makeText(this, "Viewing HDFC ERGO Health details", Toast.LENGTH_SHORT).show();
            // Show plan details
        } else if (id == R.id.btnBuyPlan1) {
            Toast.makeText(this, "Buying Star Health Insurance plan", Toast.LENGTH_SHORT).show();
            // Start purchase process
        } else if (id == R.id.btnBuyPlan2) {
            Toast.makeText(this, "Buying HDFC ERGO Health plan", Toast.LENGTH_SHORT).show();
            // Start purchase process
        } else if (id == R.id.btnCompareAll) {
            Toast.makeText(this, "Comparing all health insurance plans", Toast.LENGTH_SHORT).show();
            // Show comparison of all plans
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekBarCoverage) {
            int coverage = getCoverageFromProgress(progress);
            updateCoverageUI(coverage);
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
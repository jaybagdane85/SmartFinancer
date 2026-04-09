package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.NumberFormat;
import java.util.Locale;

public class GoldLoanActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private RadioGroup radioGroupGoldType;
    private RadioButton radioButton22K, radioButton24K;
    private EditText etGoldWeight;
    private Spinner spinnerGoldPurity;
    private Button btnCalculateGoldValue, btnApplyGoldLoan;
    private TextView tvGoldValue, tvEligibleLoanAmount;
    private Button btnApplyProvider1, btnApplyProvider2, btnViewAllProviders;
    private CardView cardProvider1, cardProvider2;

    // Constants for gold value calculation
    private static final double GOLD_PRICE_22K = 5750; // per gram
    private static final double GOLD_PRICE_24K = 6250; // per gram
    private static final double LOAN_TO_VALUE_RATIO = 0.75; // 75% of gold value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold_loan);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize gold purity spinner
        initGoldPuritySpinner();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Gold calculator views
        radioGroupGoldType = findViewById(R.id.radioGroupGoldType);
        radioButton22K = findViewById(R.id.radioButton22K);
        radioButton24K = findViewById(R.id.radioButton24K);
        etGoldWeight = findViewById(R.id.etGoldWeight);
        spinnerGoldPurity = findViewById(R.id.spinnerGoldPurity);
        btnCalculateGoldValue = findViewById(R.id.btnCalculateGoldValue);
        tvGoldValue = findViewById(R.id.tvGoldValue);
        tvEligibleLoanAmount = findViewById(R.id.tvEligibleLoanAmount);
        btnApplyGoldLoan = findViewById(R.id.btnApplyGoldLoan);

        // Provider views
        cardProvider1 = findViewById(R.id.cardProvider1);
        cardProvider2 = findViewById(R.id.cardProvider2);
        btnApplyProvider1 = findViewById(R.id.btnApplyProvider1);
        btnApplyProvider2 = findViewById(R.id.btnApplyProvider2);
        btnViewAllProviders = findViewById(R.id.btnViewAllProviders);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);
        btnCalculateGoldValue.setOnClickListener(this);
        btnApplyGoldLoan.setOnClickListener(this);
        cardProvider1.setOnClickListener(this);
        cardProvider2.setOnClickListener(this);
        btnApplyProvider1.setOnClickListener(this);
        btnApplyProvider2.setOnClickListener(this);
        btnViewAllProviders.setOnClickListener(this);
    }

    private void initGoldPuritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gold_purity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoldPurity.setAdapter(adapter);
    }

    private void calculateGoldValue() {
        String weightText = etGoldWeight.getText().toString().trim();
        if (weightText.isEmpty()) {
            Toast.makeText(this, "Please enter gold weight", Toast.LENGTH_SHORT).show();
            return;
        }

        double goldWeight = Double.parseDouble(weightText);
        int selectedId = radioGroupGoldType.getCheckedRadioButtonId();
        boolean is24K = selectedId == R.id.radioButton24K;

        String purityString = spinnerGoldPurity.getSelectedItem().toString();
        double purityFactor = getPurityFactor(purityString);

        double goldPrice = is24K ? GOLD_PRICE_24K : GOLD_PRICE_22K;
        double goldValue = goldWeight * goldPrice * purityFactor;
        double eligibleLoanAmount = goldValue * LOAN_TO_VALUE_RATIO;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        tvGoldValue.setText(formatter.format(goldValue));
        tvEligibleLoanAmount.setText(formatter.format(eligibleLoanAmount));
    }

    private double getPurityFactor(String purityString) {
        if (purityString.contains("%")) {
            String percentageStr = purityString.substring(0, purityString.indexOf("%"));
            try {
                double percentage = Double.parseDouble(percentageStr);
                return percentage / 100.0;
            } catch (NumberFormatException e) {
                return 1.0;
            }
        }
        return 1.0;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnCalculateGoldValue) {
            calculateGoldValue();
        } else if (id == R.id.btnApplyGoldLoan) {
            Toast.makeText(this, "Applying for Gold Loan", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.cardProvider1 || id == R.id.btnApplyProvider1) {
            Toast.makeText(this, "Applying with Muthoot Finance", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.cardProvider2 || id == R.id.btnApplyProvider2) {
            Toast.makeText(this, "Applying with Manappuram Finance", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnViewAllProviders) {
            Toast.makeText(this, "View All Gold Loan Providers", Toast.LENGTH_SHORT).show();
        }
    }
}

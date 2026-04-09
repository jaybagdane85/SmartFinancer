package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PersonalLoanActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnBack;
    private SeekBar seekBarLoanAmount, seekBarLoanTenure;
    private TextView tvLoanAmount, tvLoanTenure, tvInterestRate, tvMonthlyEMI, tvPrincipal, tvTotalInterest;
    private Button btnApplyNow, btnApplyOffer1, btnApplyOffer2, btnViewAllOffers, btnCheckEligibility;
    private CardView cardOffer1, cardOffer2;

    // Constants for loan calculation
    private static final int MIN_LOAN_AMOUNT = 50000;
    private static final int MAX_LOAN_AMOUNT = 1000000;
    private static final int MIN_LOAN_TENURE = 12;
    private static final int MAX_LOAN_TENURE = 60;
    private static final double INTEREST_RATE = 10.5; // in percentage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_loan);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize loan calculator
        initLoanCalculator();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Loan calculator views
        seekBarLoanAmount = findViewById(R.id.seekBarLoanAmount);
        seekBarLoanTenure = findViewById(R.id.seekBarLoanTenure);
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvLoanTenure = findViewById(R.id.tvLoanTenure);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvMonthlyEMI = findViewById(R.id.tvMonthlyEMI);
        tvPrincipal = findViewById(R.id.tvPrincipal);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);

        // Loan offer views
        cardOffer1 = findViewById(R.id.cardOffer1);
        cardOffer2 = findViewById(R.id.cardOffer2);

        // Buttons
        btnApplyNow = findViewById(R.id.btnApplyNow);
        btnApplyOffer1 = findViewById(R.id.btnApplyOffer1);
        btnApplyOffer2 = findViewById(R.id.btnApplyOffer2);
        btnViewAllOffers = findViewById(R.id.btnViewAllOffers);
        btnCheckEligibility = findViewById(R.id.btnCheckEligibility);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Seek bars
        seekBarLoanAmount.setOnSeekBarChangeListener(this);
        seekBarLoanTenure.setOnSeekBarChangeListener(this);

        // Loan offer views
        cardOffer1.setOnClickListener(this);
        cardOffer2.setOnClickListener(this);

        // Buttons
        btnApplyNow.setOnClickListener(this);
        btnApplyOffer1.setOnClickListener(this);
        btnApplyOffer2.setOnClickListener(this);
        btnViewAllOffers.setOnClickListener(this);
        btnCheckEligibility.setOnClickListener(this);
    }

    private void initLoanCalculator() {
        // Set initial values
        seekBarLoanAmount.setProgress(50); // 50% of the range
        seekBarLoanTenure.setProgress(24); // 24 months

        // Update UI with initial values
        updateLoanAmountUI(getLoanAmountFromProgress(seekBarLoanAmount.getProgress()));
        updateLoanTenureUI(getLoanTenureFromProgress(seekBarLoanTenure.getProgress()));

        // Calculate and update EMI
        calculateEMI();
    }

    private int getLoanAmountFromProgress(int progress) {
        // Convert progress (0-100) to loan amount
        return MIN_LOAN_AMOUNT + (progress * (MAX_LOAN_AMOUNT - MIN_LOAN_AMOUNT) / 100);
    }

    private int getLoanTenureFromProgress(int progress) {
        // Convert progress (0-48) to loan tenure
        return MIN_LOAN_TENURE + progress;
    }

    private void updateLoanAmountUI(int loanAmount) {
        // Format loan amount as currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formattedAmount = formatter.format(loanAmount).replace("₹", "₹");
        tvLoanAmount.setText(formattedAmount);
        tvPrincipal.setText(formattedAmount);
    }

    private void updateLoanTenureUI(int loanTenure) {
        tvLoanTenure.setText(loanTenure + " months");
    }

    private void calculateEMI() {
        int loanAmount = getLoanAmountFromProgress(seekBarLoanAmount.getProgress());
        int loanTenure = getLoanTenureFromProgress(seekBarLoanTenure.getProgress());

        // EMI calculation formula: EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
        // where P = Principal, R = Monthly interest rate, N = Number of months

        double monthlyInterestRate = INTEREST_RATE / (12 * 100);
        double emi = (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTenure)) /
                (Math.pow(1 + monthlyInterestRate, loanTenure) - 1);

        double totalAmount = emi * loanTenure;
        double totalInterest = totalAmount - loanAmount;

        // Format and update UI
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        DecimalFormat decimalFormat = new DecimalFormat("#,##,###");

        String formattedEMI = formatter.format(emi).replace("₹", "₹");
        String formattedTotalInterest = formatter.format(totalInterest).replace("₹", "₹");

        tvMonthlyEMI.setText(formattedEMI);
        tvTotalInterest.setText(formattedTotalInterest);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Loan calculator buttons
        else if (id == R.id.btnApplyNow) {
            int loanAmount = getLoanAmountFromProgress(seekBarLoanAmount.getProgress());
            int loanTenure = getLoanTenureFromProgress(seekBarLoanTenure.getProgress());

            Toast.makeText(this, "Applying for loan of ₹" + loanAmount + " for " + loanTenure + " months", Toast.LENGTH_SHORT).show();
            // Start loan application process
        }
        // Loan offer views and buttons
        else if (id == R.id.cardOffer1 || id == R.id.btnApplyOffer1) {
            Toast.makeText(this, "Applying for HDFC Bank Personal Loan", Toast.LENGTH_SHORT).show();
            // Start HDFC loan application process
        } else if (id == R.id.cardOffer2 || id == R.id.btnApplyOffer2) {
            Toast.makeText(this, "Applying for ICICI Bank Personal Loan", Toast.LENGTH_SHORT).show();
            // Start ICICI loan application process
        } else if (id == R.id.btnViewAllOffers) {
            Toast.makeText(this, "View All Loan Offers", Toast.LENGTH_SHORT).show();
            // Show all loan offers
        } else if (id == R.id.btnCheckEligibility) {
            Toast.makeText(this, "Checking Loan Eligibility", Toast.LENGTH_SHORT).show();
            // Start eligibility check process
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();

        if (id == R.id.seekBarLoanAmount) {
            int loanAmount = getLoanAmountFromProgress(progress);
            updateLoanAmountUI(loanAmount);
        } else if (id == R.id.seekBarLoanTenure) {
            int loanTenure = getLoanTenureFromProgress(progress);
            updateLoanTenureUI(loanTenure);
        }

        // Recalculate EMI whenever any slider changes
        calculateEMI();
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
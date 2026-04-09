package com.example.smartfinancer;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class YearlyTransactionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartFinancerPrefs";

    private TextView textYearlyTotal;
    private TextView textYearlyLimitValue;
    private TextView textYearlyRemaining;
    private TextView textNoTransactions;
    private RecyclerView recyclerYearlyTransactions;

    private int yearlyLimit = 36000;
    private int yearlyExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearly_transaction);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        textYearlyTotal = findViewById(R.id.textYearlyTotal);
        textYearlyLimitValue = findViewById(R.id.textYearlyLimitValue);
        textYearlyRemaining = findViewById(R.id.textYearlyRemaining);
        textNoTransactions = findViewById(R.id.textNoTransactions);
        recyclerYearlyTransactions = findViewById(R.id.recyclerYearlyTransactions);

        // Setup RecyclerView
        recyclerYearlyTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Load yearly limit
        loadYearlyLimit();

        // Read SMS and get transactions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            readSMSAndGetYearlyTransactions();
        } else {
            // Load sample data if permission not granted
            loadSampleData();
        }
    }

    private void loadYearlyLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        yearlyLimit = prefs.getInt("yearly_limit", 36000);
        textYearlyLimitValue.setText("₹" + String.format(Locale.getDefault(), "%,d", yearlyLimit));
    }

    private void readSMSAndGetYearlyTransactions() {
        // Implementation similar to DailyTransactionActivity but for yearly transactions
        // This would filter SMS messages from the current year
        loadSampleData(); // For demonstration
    }

    private void loadSampleData() {
        // Sample data for demonstration
        yearlyExpense = 28500;
        textYearlyTotal.setText("₹" + String.format(Locale.getDefault(), "%,d", yearlyExpense));

        int remaining = yearlyLimit - yearlyExpense;
        textYearlyRemaining.setText("₹" + String.format(Locale.getDefault(), "%,d", remaining));

        // Change color based on remaining amount
        if (remaining < 0) {
            textYearlyRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            textYearlyRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // Show no transactions message
        recyclerYearlyTransactions.setVisibility(View.GONE);
        textNoTransactions.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

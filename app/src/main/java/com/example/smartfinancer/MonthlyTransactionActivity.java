package com.example.smartfinancer;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyTransactionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartFinancerPrefs";

    private TextView textMonthlyTotal;
    private TextView textMonthlyLimitValue;
    private TextView textMonthlyRemaining;
    private TextView textNoTransactions;
    private RecyclerView recyclerMonthlyTransactions;

    private int monthlyLimit = 3000;
    private int monthlyExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_transaction);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        textMonthlyTotal = findViewById(R.id.textMonthlyTotal);
        textMonthlyLimitValue = findViewById(R.id.textMonthlyLimitValue);
        textMonthlyRemaining = findViewById(R.id.textMonthlyRemaining);
        textNoTransactions = findViewById(R.id.textNoTransactions);
        recyclerMonthlyTransactions = findViewById(R.id.recyclerMonthlyTransactions);

        // Setup RecyclerView
        recyclerMonthlyTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Load monthly limit
        loadMonthlyLimit();

        // Read SMS and get transactions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            readSMSAndGetMonthlyTransactions();
        } else {
            // Load sample data if permission not granted
            loadSampleData();
        }
    }

    private void loadMonthlyLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        monthlyLimit = prefs.getInt("monthly_limit", 3000);
        textMonthlyLimitValue.setText("₹" + String.format(Locale.getDefault(), "%,d", monthlyLimit));
    }

    private void readSMSAndGetMonthlyTransactions() {
        // Implementation similar to DailyTransactionActivity but for monthly transactions
        // This would filter SMS messages from the current month
        loadSampleData(); // For demonstration
    }

    private void loadSampleData() {
        // Sample data for demonstration
        monthlyExpense = 2450;
        textMonthlyTotal.setText("₹" + String.format(Locale.getDefault(), "%,d", monthlyExpense));

        int remaining = monthlyLimit - monthlyExpense;
        textMonthlyRemaining.setText("₹" + String.format(Locale.getDefault(), "%,d", remaining));

        // Change color based on remaining amount
        if (remaining < 0) {
            textMonthlyRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            textMonthlyRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // Show no transactions message
        recyclerMonthlyTransactions.setVisibility(View.GONE);
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

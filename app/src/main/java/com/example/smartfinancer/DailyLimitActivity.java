package com.example.smartfinancer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DailyLimitActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartFinancerPrefs";
    private static final String KEY_DAILY_LIMIT = "daily_limit";

    private TextView textCurrentDailyLimit;
    private EditText editNewDailyLimit;
    private Button buttonSaveDailyLimit;
    private Button buttonLimit50;
    private Button buttonLimit100;
    private Button buttonLimit200;
    private Button buttonLimit500;
    private Button buttonLimit1000;
    private Button buttonLimit2000;

    private int currentLimit = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_limit);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Set Daily Limit");
        }

        // Initialize views
        textCurrentDailyLimit = findViewById(R.id.textCurrentDailyLimit);
        editNewDailyLimit = findViewById(R.id.editNewDailyLimit);
        buttonSaveDailyLimit = findViewById(R.id.buttonSaveDailyLimit);
        buttonLimit50 = findViewById(R.id.buttonLimit50);
        buttonLimit100 = findViewById(R.id.buttonLimit100);
        buttonLimit200 = findViewById(R.id.buttonLimit200);
        buttonLimit500 = findViewById(R.id.buttonLimit500);
        buttonLimit1000 = findViewById(R.id.buttonLimit1000);
        buttonLimit2000 = findViewById(R.id.buttonLimit2000);

        // Load current limit
        loadCurrentLimit();

        // Setup button click listeners
        buttonSaveDailyLimit.setOnClickListener(v -> saveNewLimit());
        buttonLimit50.setOnClickListener(v -> setPresetLimit(50));
        buttonLimit100.setOnClickListener(v -> setPresetLimit(100));
        buttonLimit200.setOnClickListener(v -> setPresetLimit(200));
        buttonLimit500.setOnClickListener(v -> setPresetLimit(500));
        buttonLimit1000.setOnClickListener(v -> setPresetLimit(1000));
        buttonLimit2000.setOnClickListener(v -> setPresetLimit(2000));
    }

    private void loadCurrentLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentLimit = prefs.getInt(KEY_DAILY_LIMIT, 100);
        textCurrentDailyLimit.setText("₹" + currentLimit);
    }

    private void saveNewLimit() {
        String limitStr = editNewDailyLimit.getText().toString().trim();
        if (limitStr.isEmpty()) {
            Toast.makeText(this, "Please enter a limit amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int newLimit = Integer.parseInt(limitStr);
            if (newLimit <= 0) {
                Toast.makeText(this, "Limit must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the new limit
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(KEY_DAILY_LIMIT, newLimit);
            editor.apply();

            // Update UI
            currentLimit = newLimit;
            textCurrentDailyLimit.setText("₹" + currentLimit);
            editNewDailyLimit.setText("");

            Toast.makeText(this, "Daily limit updated successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPresetLimit(int limit) {
        // Save the preset limit
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_DAILY_LIMIT, limit);
        editor.apply();

        // Update UI
        currentLimit = limit;
        textCurrentDailyLimit.setText("₹" + currentLimit);
        editNewDailyLimit.setText("");

        Toast.makeText(this, "Daily limit set to ₹" + limit, Toast.LENGTH_SHORT).show();
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

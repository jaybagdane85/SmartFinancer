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

import java.util.Locale;

public class YearlyLimitActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartFinancerPrefs";

    private TextView textCurrentLimit;
    private EditText editNewLimit;
    private Button buttonSaveLimit;
    private Button buttonLimit36000;
    private Button buttonLimit60000;
    private Button buttonLimit100000;

    private int currentLimit = 36000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearly_limit);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        textCurrentLimit = findViewById(R.id.textCurrentLimit);
        editNewLimit = findViewById(R.id.editNewLimit);
        buttonSaveLimit = findViewById(R.id.buttonSaveLimit);
        buttonLimit36000 = findViewById(R.id.buttonLimit36000);
        buttonLimit60000 = findViewById(R.id.buttonLimit60000);
        buttonLimit100000 = findViewById(R.id.buttonLimit100000);

        // Load current limit
        loadCurrentLimit();

        // Setup button click listeners
        buttonSaveLimit.setOnClickListener(v -> saveNewLimit());
        buttonLimit36000.setOnClickListener(v -> setPresetLimit(36000));
        buttonLimit60000.setOnClickListener(v -> setPresetLimit(60000));
        buttonLimit100000.setOnClickListener(v -> setPresetLimit(100000));
    }

    private void loadCurrentLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentLimit = prefs.getInt("yearly_limit", 36000);
        textCurrentLimit.setText("₹" + String.format(Locale.getDefault(), "%,d", currentLimit));
    }

    private void saveNewLimit() {
        String limitStr = editNewLimit.getText().toString().trim();
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
            editor.putInt("yearly_limit", newLimit);
            editor.apply();

            // Update UI
            currentLimit = newLimit;
            textCurrentLimit.setText("₹" + String.format(Locale.getDefault(), "%,d", currentLimit));
            editNewLimit.setText("");

            Toast.makeText(this, "Yearly limit updated successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPresetLimit(int limit) {
        // Save the preset limit
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("yearly_limit", limit);
        editor.apply();

        // Update UI
        currentLimit = limit;
        textCurrentLimit.setText("₹" + String.format(Locale.getDefault(), "%,d", currentLimit));
        editNewLimit.setText("");

        Toast.makeText(this, "Yearly limit set to ₹" + String.format(Locale.getDefault(), "%,d", limit), Toast.LENGTH_SHORT).show();
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

package com.example.smartfinancer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class WeeklyExpensesActivity extends AppCompatActivity {

    private TextView weeklyExpenseDisplay;
    private EditText weeklyLimitInput;
    private Button setWeeklyLimitBtn;
    private float weeklyLimit = 0;
    private static final String PREFS_NAME = "SmartFinancerPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_expenses);

        weeklyExpenseDisplay = findViewById(R.id.weekly_expense_display);
        weeklyLimitInput = findViewById(R.id.weekly_limit_input);
        setWeeklyLimitBtn = findViewById(R.id.set_weekly_limit_btn);

        loadWeeklyLimit();
        weeklyExpenseDisplay.setText("Current Weekly Limit: ₹" + weeklyLimit);

        setWeeklyLimitBtn.setOnClickListener(v -> {
            try {
                weeklyLimit = Float.parseFloat(weeklyLimitInput.getText().toString());
                saveWeeklyLimit();
                weeklyExpenseDisplay.setText("Updated Weekly Limit: ₹" + weeklyLimit);
                Toast.makeText(this, "Weekly Limit Set Successfully", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeeklyLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        weeklyLimit = prefs.getFloat("weeklyLimit", 500.0f);
    }

    private void saveWeeklyLimit() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putFloat("weeklyLimit", weeklyLimit);
        editor.apply();
    }
}

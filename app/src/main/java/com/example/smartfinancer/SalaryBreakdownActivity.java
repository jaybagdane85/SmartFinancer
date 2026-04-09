package com.example.smartfinancer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.text.NumberFormat;
import java.util.Locale;

public class SalaryBreakdownActivity extends AppCompatActivity {

    private EditText editSalary;
    private Button buttonCalculate;
    private CardView cardBreakdown;
    private TextView textNecessities, textSavings, textWants;
    private TextView textRent, textGroceries, textTransportation;
    private TextView textEmergency, textInvestments, textEntertainment, textMiscellaneous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_breakdown);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        editSalary = findViewById(R.id.editSalary);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        cardBreakdown = findViewById(R.id.cardBreakdown);
        textNecessities = findViewById(R.id.textNecessities);
        textSavings = findViewById(R.id.textSavings);
        textWants = findViewById(R.id.textWants);
        textRent = findViewById(R.id.textRent);
        textGroceries = findViewById(R.id.textGroceries);
        textTransportation = findViewById(R.id.textTransportation);
        textEmergency = findViewById(R.id.textEmergency);
        textInvestments = findViewById(R.id.textInvestments);
        textEntertainment = findViewById(R.id.textEntertainment);
        textMiscellaneous = findViewById(R.id.textMiscellaneous);

        // Setup button click listener
        buttonCalculate.setOnClickListener(v -> calculateBreakdown());
    }

    private void calculateBreakdown() {
        String salaryStr = editSalary.getText().toString().trim();
        if (salaryStr.isEmpty()) {
            Toast.makeText(this, "Please enter your salary", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int salary = Integer.parseInt(salaryStr);
            if (salary <= 0) {
                Toast.makeText(this, "Salary must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate breakdown based on 50/30/20 rule
            int necessities = (int) (salary * 0.5); // 50% for necessities
            int savings = (int) (salary * 0.3);     // 30% for savings
            int wants = (int) (salary * 0.2);       // 20% for wants

            // Calculate detailed breakdown
            int rent = (int) (salary * 0.25);           // 25% for rent/housing
            int groceries = (int) (salary * 0.15);      // 15% for groceries
            int transportation = (int) (salary * 0.1);  // 10% for transportation
            int emergency = (int) (salary * 0.1);       // 10% for emergency fund
            int investments = (int) (salary * 0.2);     // 20% for investments
            int entertainment = (int) (salary * 0.1);   // 10% for entertainment
            int miscellaneous = (int) (salary * 0.1);   // 10% for miscellaneous

            // Format currency
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

            // Update UI
            textNecessities.setText(format.format(necessities));
            textSavings.setText(format.format(savings));
            textWants.setText(format.format(wants));
            textRent.setText(format.format(rent));
            textGroceries.setText(format.format(groceries));
            textTransportation.setText(format.format(transportation));
            textEmergency.setText(format.format(emergency));
            textInvestments.setText(format.format(investments));
            textEntertainment.setText(format.format(entertainment));
            textMiscellaneous.setText(format.format(miscellaneous));

            // Show breakdown card
            cardBreakdown.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
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

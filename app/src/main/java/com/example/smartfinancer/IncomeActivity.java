package com.example.smartfinancer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IncomeActivity extends AppCompatActivity {

    private TextView tvMonthIncome, tvLastMonthIncome, tvIncomeChange;
    private LinearLayout incomeSourcesContainer;
    private RecyclerView rvIncomeHistory;
    private EditText etIncomeSource, etIncomeAmount, etIncomeDate;
    private Button btnAddIncome;
    private BottomNavigationView bottomNavigationView;

    private List<IncomeEntry> incomeEntries = new ArrayList<>();
    private IncomeAdapter adapter;

    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

    private double currentMonthIncome = 45000;
    private double lastMonthIncome = 42000;
    private double totalIncome = 0;
    private Map<String, Double> sourceIncomes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        // Initialize views
        tvMonthIncome = findViewById(R.id.tv_month_income);
        tvLastMonthIncome = findViewById(R.id.tv_last_month_income);
        tvIncomeChange = findViewById(R.id.tv_income_change);
        incomeSourcesContainer = findViewById(R.id.income_sources_container);
        rvIncomeHistory = findViewById(R.id.rv_income_history);
        etIncomeSource = findViewById(R.id.et_income_source);
        etIncomeAmount = findViewById(R.id.et_income_amount);
        etIncomeDate = findViewById(R.id.et_income_date);
        btnAddIncome = findViewById(R.id.btn_add_income);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup RecyclerView
        rvIncomeHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IncomeAdapter(incomeEntries);
        rvIncomeHistory.setAdapter(adapter);

        // Setup date picker
        etIncomeDate.setText(dateFormat.format(selectedDate.getTime()));
        etIncomeDate.setOnClickListener(v -> showDatePicker());

        // Setup add income button
        btnAddIncome.setOnClickListener(v -> addIncome());

        // Load sample data
        loadSampleData();

        // Update UI
        updateIncomeSummary();
        updateIncomeSourcesView();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_income1);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_income1) {
                return true;
            } else if (id == R.id.nav_expenses1) {
                startActivity(new Intent(this, ExpenseActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_home1) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_goal1) {
                startActivity(new Intent(this, GoalSettingActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile1) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etIncomeDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addIncome() {
        String source = etIncomeSource.getText().toString().trim();
        String amountStr = etIncomeAmount.getText().toString().trim();

        if (source.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            IncomeEntry entry = new IncomeEntry(source, amount, selectedDate.getTimeInMillis());

            // Add to income entries list
            incomeEntries.add(0, entry); // Add to the beginning of the list

            // Update source incomes
            if (sourceIncomes.containsKey(source)) {
                sourceIncomes.put(source, sourceIncomes.get(source) + amount);
            } else {
                sourceIncomes.put(source, amount);
            }

            // Update total income
            totalIncome += amount;

            // Update current month income if the entry is from current month
            Calendar entryDate = Calendar.getInstance();
            entryDate.setTimeInMillis(entry.getTimestamp());

            Calendar now = Calendar.getInstance();
            if (entryDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    entryDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                currentMonthIncome += amount;
            }

            // Update UI
            updateIncomeSummary();
            updateIncomeSourcesView();

            // Update RecyclerView
            adapter.notifyItemInserted(0);
            rvIncomeHistory.scrollToPosition(0);

            // Clear fields
            etIncomeSource.setText("");
            etIncomeAmount.setText("");
            selectedDate = Calendar.getInstance();
            etIncomeDate.setText(dateFormat.format(selectedDate.getTime()));

            Toast.makeText(this, "Income added successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIncomeSummary() {
        tvMonthIncome.setText("₹" + String.format(Locale.getDefault(), "%,.0f", currentMonthIncome));
        tvLastMonthIncome.setText("₹" + String.format(Locale.getDefault(), "%,.0f", lastMonthIncome));

        // Calculate percentage change
        double percentChange = ((currentMonthIncome - lastMonthIncome) / lastMonthIncome) * 100;
        String changeText = String.format(Locale.getDefault(), "%+.2f%% from last month", percentChange);
        tvIncomeChange.setText(changeText);

        // Set color based on positive or negative change
        if (percentChange >= 0) {
            tvIncomeChange.setTextColor(getResources().getColor(R.color.colorIncome));
        } else {
            tvIncomeChange.setTextColor(getResources().getColor(R.color.colorExpense));
        }
    }

    private void updateIncomeSourcesView() {
        incomeSourcesContainer.removeAllViews();

        if (sourceIncomes.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No income data available");
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            emptyView.setTextColor(getResources().getColor(R.color.colorGray));
            emptyView.setPadding(16, 16, 16, 16);
            incomeSourcesContainer.addView(emptyView);
            return;
        }

        // Sort sources by amount (descending)
        List<Map.Entry<String, Double>> sortedSources = new ArrayList<>(sourceIncomes.entrySet());
        Collections.sort(sortedSources, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Create a LinkedHashMap to maintain order
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : sortedSources) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        // Add source items to container
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            View sourceView = inflater.inflate(R.layout.item_category, incomeSourcesContainer, false);

            TextView tvSourceName = sourceView.findViewById(R.id.tv_category_name);
            TextView tvSourceAmount = sourceView.findViewById(R.id.tv_category_amount);
            ProgressBar progressSource = sourceView.findViewById(R.id.progress_category);
            TextView tvSourcePercentage = sourceView.findViewById(R.id.tv_category_percentage);

            String source = entry.getKey();
            double amount = entry.getValue();
            int percentage = (int) ((amount / totalIncome) * 100);

            tvSourceName.setText(source);
            tvSourceAmount.setText("₹" + String.format(Locale.getDefault(), "%,.2f", amount));
            progressSource.setProgress(percentage);
            progressSource.setProgressTintList(getResources().getColorStateList(R.color.colorIncome));
            tvSourcePercentage.setText(percentage + "% of total");

            incomeSourcesContainer.addView(sourceView);
        }
    }

    private void loadSampleData() {
        // Add sample income entries
        incomeEntries.add(new IncomeEntry("Salary", 45000, System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
        incomeEntries.add(new IncomeEntry("Freelance", 12000, System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000));
        incomeEntries.add(new IncomeEntry("Investments", 8000, System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000));
        incomeEntries.add(new IncomeEntry("Rent", 15000, System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000));

        // Calculate source incomes
        for (IncomeEntry entry : incomeEntries) {
            String source = entry.getSource();
            double amount = entry.getAmount();

            if (sourceIncomes.containsKey(source)) {
                sourceIncomes.put(source, sourceIncomes.get(source) + amount);
            } else {
                sourceIncomes.put(source, amount);
            }

            totalIncome += amount;
        }

        adapter.notifyDataSetChanged();
    }

    // Income Entry model class
    private static class IncomeEntry {
        private String source;
        private double amount;
        private long timestamp;

        public IncomeEntry(String source, double amount, long timestamp) {
            this.source = source;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public String getSource() {
            return source;
        }

        public double getAmount() {
            return amount;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    // Adapter for income history
    private class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

        private List<IncomeEntry> entries;

        public IncomeAdapter(List<IncomeEntry> entries) {
            this.entries = entries;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            IncomeEntry entry = entries.get(position);

            holder.tvTitle.setText(entry.getSource());

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
            String date = sdf.format(new Date(entry.getTimestamp()));
            holder.tvDate.setText(date);

            String amountText = "+₹" + String.format(Locale.getDefault(), "%.2f", entry.getAmount());
            holder.tvAmount.setText(amountText);
            holder.tvAmount.setTextColor(getResources().getColor(R.color.colorIncome));
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvAmount;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_transaction_title);
                tvDate = itemView.findViewById(R.id.tv_transaction_date);
                tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
            }
        }
    }
}


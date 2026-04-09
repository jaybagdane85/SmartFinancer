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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyTransactionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartFinancerPrefs";
    private static final String KEY_DAILY_LIMIT = "daily_limit";
    private static final String KEY_DAILY_SPENT = "daily_spent";

    private TextView textDailyTotal;
    private TextView textDailyLimitValue;
    private TextView textDailyRemaining;
    private TextView textNoTransactions;
    private RecyclerView recyclerDailyTransactions;

    private int dailyLimit = 100;
    private double todayExpense = 0;
    private List<Transaction> dailyTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_transaction);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daily Transactions");

        // Initialize views
        textDailyTotal = findViewById(R.id.textDailyTotal);
        textDailyLimitValue = findViewById(R.id.textDailyLimitValue);
        textDailyRemaining = findViewById(R.id.textDailyRemaining);
        textNoTransactions = findViewById(R.id.textNoTransactions);
        recyclerDailyTransactions = findViewById(R.id.recyclerDailyTransactions);

        // Setup RecyclerView
        recyclerDailyTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Load daily limit
        loadDailyLimit();

        // Read SMS and get transactions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            readSMSAndGetDailyTransactions();
        } else {
            // Load sample data if permission not granted
            loadSampleData();
        }
    }

    private void loadDailyLimit() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dailyLimit = prefs.getInt(KEY_DAILY_LIMIT, 100);
        textDailyLimitValue.setText("₹" + dailyLimit);
    }

    private void readSMSAndGetDailyTransactions() {
        dailyTransactions.clear();

        // Get today's spent amount from shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        todayExpense = prefs.getFloat(KEY_DAILY_SPENT, 0);

        // Reset daily spent if it's a new day
        if (isNewDay(prefs)) {
            todayExpense = 0;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_DAILY_SPENT, 0);
            editor.putLong("last_reset_time", System.currentTimeMillis());
            editor.apply();
        }

        Uri smsUri = Telephony.Sms.Inbox.CONTENT_URI;

        // Create a filter for today's messages only
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTimeInMillis();

        String selection = Telephony.Sms.DATE + " >= ?";
        String[] selectionArgs = new String[] { String.valueOf(todayStart) };

        Cursor cursor = getContentResolver().query(
                smsUri,
                null,
                selection,
                selectionArgs,
                Telephony.Sms.DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                Date messageDate = new Date(timestamp);

                // Check if it's a valid transaction message from a bank
                if (isValidTransactionMessage(body) && isBankSender(sender)) {
                    double amount = extractAmount(body);
                    if (amount > 0) {
                        // Create transaction object
                        String merchant = extractMerchantName(body);
                        String category = determineCategoryFromMessage(body);
                        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(messageDate);

                        Transaction transaction = new Transaction(merchant, category, amount, time);
                        dailyTransactions.add(transaction);
                    }
                }
            }
            cursor.close();
        }

        updateUI();
    }

    private boolean isNewDay(SharedPreferences prefs) {
        long lastResetTime = prefs.getLong("last_reset_time", 0);
        if (lastResetTime == 0) {
            return true;
        }

        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastResetTime);

        Calendar now = Calendar.getInstance();

        return lastReset.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) ||
                lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR);
    }

    private boolean isValidTransactionMessage(String message) {
        return message.toLowerCase().contains("debited") ||
                message.toLowerCase().contains("spent") ||
                message.toLowerCase().contains("paid") ||
                message.toLowerCase().contains("purchase") ||
                message.matches(".*\\bINR\\s?\\d+.*") ||
                message.matches(".*\\bRs\\.?\\s?\\d+.*") ||
                message.matches(".*\\b₹\\s?\\d+.*");
    }

    private boolean isBankSender(String sender) {
        if (sender == null) return false;
        sender = sender.toUpperCase();
        String[] bankSenders = {"SBI", "HDFC", "ICICI", "AXIS", "KOTAK", "IDFC", "PAYTM", "UPI", "MAHABK", "CANBNK"};

        for (String bankSender : bankSenders) {
            if (sender.contains(bankSender)) {
                return true;
            }
        }
        return false;
    }

    private double extractAmount(String message) {
        try {
            message = message.replace(",", "");

            // Pattern to match INR/Rs/₹ followed by digits with optional decimal point
            Pattern pattern = Pattern.compile("(?:INR|Rs\\.?|₹)\\s?([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                String amountStr = matcher.group(1);
                return Double.parseDouble(amountStr);
            }

            // Try to match "amount" followed by INR/Rs/₹ and digits
            pattern = Pattern.compile("(?:amount|amt)\\s+(?:of\\s+)?(?:INR|Rs\\.?|₹)\\s?([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(message);

            if (matcher.find()) {
                String amountStr = matcher.group(1);
                return Double.parseDouble(amountStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String extractMerchantName(String message) {
        try {
            // Try to match common patterns for merchant names
            Pattern pattern = Pattern.compile("(?:at|to|in|from)\\s+([A-Za-z0-9\\s]+)(?:\\s+on|\\s+for|\\s+via|\\s+using|\\s+by|\\.|\\s+of)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Merchant";
    }

    private String determineCategoryFromMessage(String message) {
        message = message.toLowerCase();

        if (message.contains("food") || message.contains("restaurant") || message.contains("cafe") ||
                message.contains("swiggy") || message.contains("zomato")) {
            return "Food";
        } else if (message.contains("movie") || message.contains("entertainment") ||
                message.contains("netflix") || message.contains("amazon prime")) {
            return "Entertainment";
        } else if (message.contains("uber") || message.contains("ola") ||
                message.contains("transport") || message.contains("travel")) {
            return "Transport";
        } else if (message.contains("grocery") || message.contains("supermarket") ||
                message.contains("bigbasket") || message.contains("grofers")) {
            return "Groceries";
        } else if (message.contains("bill") || message.contains("electricity") ||
                message.contains("water") || message.contains("gas") || message.contains("utility")) {
            return "Bills";
        } else if (message.contains("shopping") || message.contains("amazon") ||
                message.contains("flipkart") || message.contains("myntra")) {
            return "Shopping";
        } else if (message.contains("health") || message.contains("medical") ||
                message.contains("hospital") || message.contains("doctor")) {
            return "Health";
        } else if (message.contains("education") || message.contains("school") ||
                message.contains("college") || message.contains("course")) {
            return "Education";
        }

        return "Others";
    }

    private void updateUI() {
        // Format with proper decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedTotal = df.format(todayExpense);

        textDailyTotal.setText("₹" + formattedTotal);

        double remaining = dailyLimit - todayExpense;
        String formattedRemaining = df.format(remaining);
        textDailyRemaining.setText("₹" + formattedRemaining);

        // Change color based on remaining amount
        if (remaining < 0) {
            textDailyRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            textDailyRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // Show transactions or no transactions message
        if (dailyTransactions.isEmpty()) {
            recyclerDailyTransactions.setVisibility(View.GONE);
            textNoTransactions.setVisibility(View.VISIBLE);
        } else {
            recyclerDailyTransactions.setVisibility(View.VISIBLE);
            textNoTransactions.setVisibility(View.GONE);

            // Set adapter with transactions
            TransactionAdapter adapter = new TransactionAdapter(dailyTransactions);
            recyclerDailyTransactions.setAdapter(adapter);
        }
    }

    private void loadSampleData() {
        // Sample data for demonstration
        todayExpense = 85.50;

        // Sample transactions
        dailyTransactions.add(new Transaction("Cafe Coffee Day", "Food", 45.50, "09:30 AM"));
        dailyTransactions.add(new Transaction("Uber", "Transport", 25.00, "11:15 AM"));
        dailyTransactions.add(new Transaction("Grocery Store", "Groceries", 15.00, "04:45 PM"));

        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Transaction model class
    private static class Transaction {
        private String merchant;
        private String category;
        private double amount;
        private String time;

        public Transaction(String merchant, String category, double amount, String time) {
            this.merchant = merchant;
            this.category = category;
            this.amount = amount;
            this.time = time;
        }

        public String getMerchant() {
            return merchant;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public String getTime() {
            return time;
        }
    }

    // Adapter for transaction list
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

        private List<Transaction> transactions;

        public TransactionAdapter(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);

            holder.tvMerchant.setText(transaction.getMerchant());
            holder.tvCategory.setText(transaction.getCategory());

            // Format amount with proper decimal places
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedAmount = df.format(transaction.getAmount());
            holder.tvAmount.setText("₹" + formattedAmount);

            holder.tvTime.setText(transaction.getTime());
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMerchant, tvCategory, tvAmount, tvTime;

            ViewHolder(View itemView) {
                super(itemView);
                tvMerchant = itemView.findViewById(R.id.tvMerchant);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvTime = itemView.findViewById(R.id.tvTime);
            }
        }
    }
}

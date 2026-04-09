package com.example.smartfinancer;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String CHANNEL_ID = "expense_alert_channel";
    private static final String PREFS_NAME = "SmartFinancerPrefs";
    private static final String KEY_DAILY_LIMIT = "daily_limit";
    private static final String KEY_DAILY_SPENT = "daily_spent";
    private static final String KEY_MONTHLY_LIMIT = "monthly_limit";
    private static final String KEY_MONTHLY_SPENT = "monthly_spent";
    private static final String KEY_YEARLY_LIMIT = "yearly_limit";
    private static final String KEY_YEARLY_SPENT = "yearly_spent";

    private TextView textTodayExpense;
    private TextView textMonthlyExpense;
    private TextView textYearlyExpense;
    private TextView textDailyLimit;
    private TextView textMonthlyLimit;
    private TextView textYearlyLimit;
    private CardView cardDailyTransaction;
    private CardView cardDailyLimit;
    private CardView cardMonthlyTransaction;
    private CardView cardMonthlyLimit;
    private CardView cardYearlyTransaction;
    private CardView cardYearlyLimit;
    private CardView cardSalaryBreakdown;
    private TabLayout tabLayout;

    private int dailyLimit = 100;
    private int monthlyLimit = 3000;
    private int yearlyLimit = 36000;
    private double todayExpense = 0;
    private double monthlyExpense = 0;
    private double yearlyExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Initialize views
        initializeViews();

        // Load saved limits
        loadLimits();

        // Setup click listeners
        setupClickListeners();

        // Setup tab layout
        setupTabLayout();

        // Setup bottom navigation
        setupBottomNavigationView();

        // Create notification channel
        createNotificationChannel();

        // Check permissions and read SMS
        if (hasAllPermissions()) {
            readSMSAndCalculateExpenses();
        } else {
            requestAllPermissions();
        }
    }

    private void initializeViews() {
        textTodayExpense = findViewById(R.id.text_today_expense);
        textMonthlyExpense = findViewById(R.id.text_monthly_expense);
        textYearlyExpense = findViewById(R.id.text_yearly_expense);
        textDailyLimit = findViewById(R.id.text_daily_limit);
        textMonthlyLimit = findViewById(R.id.text_monthly_limit);
        textYearlyLimit = findViewById(R.id.text_yearly_limit);
        cardDailyTransaction = findViewById(R.id.cardDailyTransaction);
        cardDailyLimit = findViewById(R.id.cardDailyLimit);
        cardMonthlyTransaction = findViewById(R.id.cardMonthlyTransaction);
        cardMonthlyLimit = findViewById(R.id.cardMonthlyLimit);
        cardYearlyTransaction = findViewById(R.id.cardYearlyTransaction);
        cardYearlyLimit = findViewById(R.id.cardYearlyLimit);
        cardSalaryBreakdown = findViewById(R.id.cardSalaryBreakdown);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void loadLimits() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dailyLimit = prefs.getInt(KEY_DAILY_LIMIT, 100);
        monthlyLimit = prefs.getInt(KEY_MONTHLY_LIMIT, 3000);
        yearlyLimit = prefs.getInt(KEY_YEARLY_LIMIT, 36000);

        // Get current spent amounts
        todayExpense = prefs.getFloat(KEY_DAILY_SPENT, 0);
        monthlyExpense = prefs.getFloat(KEY_MONTHLY_SPENT, 0);
        yearlyExpense = prefs.getFloat(KEY_YEARLY_SPENT, 0);

        // Reset daily spent if it's a new day
        if (isNewDay(prefs)) {
            todayExpense = 0;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_DAILY_SPENT, 0);
            editor.putLong("last_reset_time", System.currentTimeMillis());
            editor.apply();
        }

        // Reset monthly spent if it's a new month
        if (isNewMonth(prefs)) {
            monthlyExpense = 0;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_MONTHLY_SPENT, 0);
            editor.putLong("last_month_reset", System.currentTimeMillis());
            editor.apply();
        }

        // Reset yearly spent if it's a new year
        if (isNewYear(prefs)) {
            yearlyExpense = 0;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_YEARLY_SPENT, 0);
            editor.putLong("last_year_reset", System.currentTimeMillis());
            editor.apply();
        }

        // Update UI with limits
        DecimalFormat df = new DecimalFormat("#,###.##");
        textDailyLimit.setText("₹" + df.format(dailyLimit));
        textMonthlyLimit.setText("₹" + df.format(monthlyLimit));
        textYearlyLimit.setText("₹" + df.format(yearlyLimit));

        // Update UI with current expenses
        textTodayExpense.setText("₹" + df.format(todayExpense));
        textMonthlyExpense.setText("₹" + df.format(monthlyExpense));
        textYearlyExpense.setText("₹" + df.format(yearlyExpense));
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

    private boolean isNewMonth(SharedPreferences prefs) {
        long lastMonthReset = prefs.getLong("last_month_reset", 0);
        if (lastMonthReset == 0) {
            return true;
        }

        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastMonthReset);

        Calendar now = Calendar.getInstance();

        return lastReset.get(Calendar.MONTH) != now.get(Calendar.MONTH) ||
                lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR);
    }

    private boolean isNewYear(SharedPreferences prefs) {
        long lastYearReset = prefs.getLong("last_year_reset", 0);
        if (lastYearReset == 0) {
            return true;
        }

        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastYearReset);

        Calendar now = Calendar.getInstance();

        return lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR);
    }

    private void setupClickListeners() {
        cardDailyTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, DailyTransactionActivity.class);
            startActivity(intent);
        });

        cardDailyLimit.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, DailyLimitActivity.class);
            startActivity(intent);
        });

        cardMonthlyTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, MonthlyTransactionActivity.class);
            startActivity(intent);
        });

        cardMonthlyLimit.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, MonthlyLimitActivity.class);
            startActivity(intent);
        });

        cardYearlyTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, YearlyTransactionActivity.class);
            startActivity(intent);
        });

        cardYearlyLimit.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, YearlyLimitActivity.class);
            startActivity(intent);
        });

        cardSalaryBreakdown.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseActivity.this, SalaryBreakdownActivity.class);
            startActivity(intent);
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) { // Alerts tab
                    showAlertsTab();
                } else {
                    showExpensesTab();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showExpensesTab() {
        // Show all expense cards
        findViewById(R.id.expensesContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.alertsContainer).setVisibility(View.GONE);
    }

    private void showAlertsTab() {
        // Show alerts and hide expense cards
        findViewById(R.id.expensesContainer).setVisibility(View.GONE);
        findViewById(R.id.alertsContainer).setVisibility(View.VISIBLE);

        // Generate alerts based on spending limits
        generateAlerts();
    }

    private void generateAlerts() {
        TextView alertsTextView = findViewById(R.id.alertsTextView);
        StringBuilder alerts = new StringBuilder();

        DecimalFormat df = new DecimalFormat("#,###.##");

        alerts.append("📊 Expense Alerts 📊\n\n");

        if (todayExpense > dailyLimit) {
            double overAmount = todayExpense - dailyLimit;
            alerts.append("⚠️ DAILY LIMIT EXCEEDED!\n");
            alerts.append("You've spent ₹").append(df.format(todayExpense))
                    .append(" today, which is ₹").append(df.format(overAmount))
                    .append(" over your daily limit of ₹").append(df.format(dailyLimit)).append("\n\n");
        } else if (todayExpense > (dailyLimit * 0.8)) {
            alerts.append("⚠️ APPROACHING DAILY LIMIT!\n");
            alerts.append("You've spent ₹").append(df.format(todayExpense))
                    .append(" today, which is ").append(Math.round((todayExpense/dailyLimit)*100))
                    .append("% of your daily limit of ₹").append(df.format(dailyLimit)).append("\n\n");
        }

        if (monthlyExpense > monthlyLimit) {
            double overAmount = monthlyExpense - monthlyLimit;
            alerts.append("⚠️ MONTHLY LIMIT EXCEEDED!\n");
            alerts.append("You've spent ₹").append(df.format(monthlyExpense))
                    .append(" this month, which is ₹").append(df.format(overAmount))
                    .append(" over your monthly limit of ₹").append(df.format(monthlyLimit)).append("\n\n");
        } else if (monthlyExpense > (monthlyLimit * 0.8)) {
            alerts.append("⚠️ APPROACHING MONTHLY LIMIT!\n");
            alerts.append("You've spent ₹").append(df.format(monthlyExpense))
                    .append(" this month, which is ").append(Math.round((monthlyExpense/monthlyLimit)*100))
                    .append("% of your monthly limit of ₹").append(df.format(monthlyLimit)).append("\n\n");
        }

        if (yearlyExpense > yearlyLimit) {
            double overAmount = yearlyExpense - yearlyLimit;
            alerts.append("⚠️ YEARLY LIMIT EXCEEDED!\n");
            alerts.append("You've spent ₹").append(df.format(yearlyExpense))
                    .append(" this year, which is ₹").append(df.format(overAmount))
                    .append(" over your yearly limit of ₹").append(df.format(yearlyLimit)).append("\n\n");
        } else if (yearlyExpense > (yearlyLimit * 0.8)) {
            alerts.append("⚠️ APPROACHING YEARLY LIMIT!\n");
            alerts.append("You've spent ₹").append(df.format(yearlyExpense))
                    .append(" this year, which is ").append(Math.round((yearlyExpense/yearlyLimit)*100))
                    .append("% of your yearly limit of ₹").append(df.format(yearlyLimit)).append("\n\n");
        }

        if (alerts.toString().equals("📊 Expense Alerts 📊\n\n")) {
            alerts.append("✅ No alerts at this time. You're within your spending limits!");
        }

        alertsTextView.setText(alerts.toString());
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_expenses1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_income1) {
                startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_expenses1) {
                return true;
            } else if (itemId == R.id.nav_home1) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_goal1) {
                startActivity(new Intent(getApplicationContext(), GoalSettingActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile1) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    private boolean hasAllPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void readSMSAndCalculateExpenses() {
        // We'll use the values from SharedPreferences that were loaded in loadLimits()
        // This is just to update the UI with the current values
        DecimalFormat df = new DecimalFormat("#,###.##");
        textTodayExpense.setText("₹" + df.format(todayExpense));
        textMonthlyExpense.setText("₹" + df.format(monthlyExpense));
        textYearlyExpense.setText("₹" + df.format(yearlyExpense));

        // Check if any limits are exceeded and update UI colors
        updateExpenseColors();
    }

    private void updateExpenseColors() {
        // Change color based on spending relative to limits
        if (todayExpense > dailyLimit) {
            textTodayExpense.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (todayExpense > (dailyLimit * 0.8)) {
            textTodayExpense.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            textTodayExpense.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        if (monthlyExpense > monthlyLimit) {
            textMonthlyExpense.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (monthlyExpense > (monthlyLimit * 0.8)) {
            textMonthlyExpense.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            textMonthlyExpense.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        if (yearlyExpense > yearlyLimit) {
            textYearlyExpense.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (yearlyExpense > (yearlyLimit * 0.8)) {
            textYearlyExpense.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            textYearlyExpense.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Alerts";
            String description = "Channel for expense alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasAllPermissions()) {
                readSMSAndCalculateExpenses();
            } else {
                Toast.makeText(this, "Permissions not granted. Some features may not work properly.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadLimits();
        if (hasAllPermissions()) {
            readSMSAndCalculateExpenses();
        }
    }
}

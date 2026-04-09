package com.example.smartfinancer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for transaction-related operations
 * This centralizes the transaction processing logic for consistency across the app
 */
public class TransactionHelper {
    private static final String TAG = "TransactionHelper";
    private static final String PREFS_NAME = "SmartFinancerPrefs";
    private static final String KEY_DAILY_LIMIT = "daily_limit";
    private static final String KEY_DAILY_SPENT = "daily_spent";
    private static final String KEY_MONTHLY_LIMIT = "monthly_limit";
    private static final String KEY_MONTHLY_SPENT = "monthly_spent";
    private static final String KEY_YEARLY_LIMIT = "yearly_limit";
    private static final String KEY_YEARLY_SPENT = "yearly_spent";

    /**
     * Check if a message is from a bank sender
     */
    public static boolean isBankSender(String sender) {
        if (sender == null) return false;
        sender = sender.toUpperCase();
        String[] bankSenders = {"SBI", "HDFC", "ICICI", "AXIS", "KOTAK", "IDFC", "PAYTM", "UPI", "MAHABK", "CANBNK", "YESBNK"};

        for (String bankSender : bankSenders) {
            if (sender.contains(bankSender)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a message is a valid transaction message
     */
    public static boolean isValidTransactionMessage(String message) {
        if (message == null) return false;

        message = message.toLowerCase();
        return message.contains("debited") ||
                message.contains("spent") ||
                message.contains("paid") ||
                message.contains("purchase") ||
                message.contains("transaction") && message.contains("debit") ||
                message.matches(".*\\bINR\\s?\\d+.*") ||
                message.matches(".*\\bRs\\.?\\s?\\d+.*") ||
                message.matches(".*\\b₹\\s?\\d+.*");
    }

    /**
     * Extract amount from a transaction message with improved accuracy
     */
    public static double extractAmount(String message) {
        try {
            if (message == null) return 0;

            // Remove commas from the message
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

            // Try to match "debited" followed by INR/Rs/₹ and digits
            pattern = Pattern.compile("(?:debited|debit)\\s+(?:with\\s+)?(?:INR|Rs\\.?|₹)\\s?([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(message);

            if (matcher.find()) {
                String amountStr = matcher.group(1);
                return Double.parseDouble(amountStr);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting amount: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Extract merchant name from a transaction message
     */
    public static String extractMerchantName(String message) {
        try {
            if (message == null) return "Unknown Merchant";

            // Try to match common patterns for merchant names
            Pattern pattern = Pattern.compile("(?:at|to|in|from)\\s+([A-Za-z0-9\\s]+)(?:\\s+on|\\s+for|\\s+via|\\s+using|\\s+by|\\.|\\s+of)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }

            // Try another pattern
            pattern = Pattern.compile("(?:purchase|payment|txn|transaction)\\s+(?:at|to|in|from)\\s+([A-Za-z0-9\\s]+)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(message);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting merchant: " + e.getMessage());
        }
        return "Unknown Merchant";
    }

    /**
     * Determine category from a transaction message
     */
    public static String determineCategoryFromMessage(String message) {
        if (message == null) return "Others";

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

    /**
     * Process a transaction and update the spending totals
     */
    public static void processTransaction(Context context, double amount, String message) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Check if we need to reset any counters
        checkAndResetCounters(context);

        // Get current values
        double dailySpent = prefs.getFloat(KEY_DAILY_SPENT, 0);
        double monthlySpent = prefs.getFloat(KEY_MONTHLY_SPENT, 0);
        double yearlySpent = prefs.getFloat(KEY_YEARLY_SPENT, 0);

        // Add the new transaction amount
        dailySpent += amount;
        monthlySpent += amount;
        yearlySpent += amount;

        // Save the updated values
        editor.putFloat(KEY_DAILY_SPENT, (float) dailySpent);
        editor.putFloat(KEY_MONTHLY_SPENT, (float) monthlySpent);
        editor.putFloat(KEY_YEARLY_SPENT, (float) yearlySpent);

        // Save transaction timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String transactionKey = "transaction_" + System.currentTimeMillis();
        editor.putString(transactionKey + "_amount", String.valueOf(amount));
        editor.putString(transactionKey + "_merchant", extractMerchantName(message));
        editor.putString(transactionKey + "_category", determineCategoryFromMessage(message));
        editor.putString(transactionKey + "_time", timestamp);
        editor.putString(transactionKey + "_message", message);

        editor.apply();
    }

    /**
     * Check and reset counters if needed (daily, monthly, yearly)
     */
    public static void checkAndResetCounters(Context context) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Check if it's a new day
        if (isNewDay(prefs)) {
            editor.putFloat(KEY_DAILY_SPENT, 0);
            editor.putLong("last_reset_time", System.currentTimeMillis());
        }

        // Check if it's a new month
        if (isNewMonth(prefs)) {
            editor.putFloat(KEY_MONTHLY_SPENT, 0);
            editor.putLong("last_month_reset", System.currentTimeMillis());
        }

        // Check if it's a new year
        if (isNewYear(prefs)) {
            editor.putFloat(KEY_YEARLY_SPENT, 0);
            editor.putLong("last_year_reset", System.currentTimeMillis());
        }

        editor.apply();
    }

    /**
     * Check if it's a new day
     */
    public static boolean isNewDay(SharedPreferences prefs) {
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

    /**
     * Check if it's a new month
     */
    public static boolean isNewMonth(SharedPreferences prefs) {
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

    /**
     * Check if it's a new year
     */
    public static boolean isNewYear(SharedPreferences prefs) {
        long lastYearReset = prefs.getLong("last_year_reset", 0);
        if (lastYearReset == 0) {
            return true;
        }

        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastYearReset);

        Calendar now = Calendar.getInstance();

        return lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR);
    }
}

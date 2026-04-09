package com.example.smartfinancer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DecimalFormat;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private static final String CHANNEL_ID = "expense_alert_channel";
    private static final String PREFS_NAME = "SmartFinancerPrefs";
    private static final String KEY_DAILY_LIMIT = "daily_limit";
    private static final String KEY_DAILY_SPENT = "daily_spent";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = smsMessage.getDisplayOriginatingAddress();
                            String message = smsMessage.getMessageBody();

                            Log.d(TAG, "SMS received from: " + sender);
                            Log.d(TAG, "Message: " + message);

                            // Check if this is a bank transaction message
                            if (TransactionHelper.isBankSender(sender) && TransactionHelper.isValidTransactionMessage(message)) {
                                double amount = TransactionHelper.extractAmount(message);
                                Log.d(TAG, "Extracted amount: " + amount);

                                if (amount > 0) {
                                    // Process the transaction
                                    TransactionHelper.processTransaction(context, amount, message);

                                    // Send notification
                                    sendTransactionNotification(context, amount, message);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing SMS: " + e.getMessage());
                }
            }
        }
    }

    private void sendTransactionNotification(Context context, double amount, String message) {
        // Create notification channel
        createNotificationChannel(context);

        // Get current daily limit and spent amount
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int dailyLimit = prefs.getInt(KEY_DAILY_LIMIT, 100);
        double dailySpent = prefs.getFloat(KEY_DAILY_SPENT, 0);

        // Format amounts with proper decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedAmount = df.format(amount);
        String formattedTotal = df.format(dailySpent);
        double remaining = dailyLimit - dailySpent;
        String formattedRemaining = df.format(remaining);

        // Extract merchant name
        String merchant = TransactionHelper.extractMerchantName(message);
        String title = "Transaction at " + merchant;

        // Create notification content
        String content = "You spent ₹" + formattedAmount +
                "\nTotal today: ₹" + formattedTotal + " of ₹" + dailyLimit +
                "\nRemaining: ₹" + formattedRemaining;

        // Create intent for when notification is tapped
        Intent intent = new Intent(context, DailyTransactionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText("You spent ₹" + formattedAmount)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Notification permission not granted: " + e.getMessage());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Alerts";
            String description = "Channel for expense alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

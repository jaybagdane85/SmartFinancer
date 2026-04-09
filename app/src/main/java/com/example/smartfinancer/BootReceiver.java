package com.example.smartfinancer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                        intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                        intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON"))) {

            Log.d(TAG, "Device boot completed, rescheduling all reminders");

            // ✅ Create notification channel
            createNotificationChannel(context);

            // ✅ Call GoalReminderReceiver WITHOUT system action
            Intent reminderIntent = new Intent(context, GoalReminderReceiver.class);
            reminderIntent.setAction("com.example.smartfinancer.RESCHEDULE_REMINDERS");
            context.sendBroadcast(reminderIntent);

            // ✅ Call Daily summary
            Intent summaryIntent = new Intent(context, DailyGoalSummaryReceiver.class);
            summaryIntent.setAction("com.example.smartfinancer.DAILY_SUMMARY");
            context.sendBroadcast(summaryIntent);
        }
    }

    private void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "goal_reminder_channel",
                    "Goal Reminders",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for financial goal reminders");
            channel.enableVibration(true);
            channel.enableLights(true);

            android.app.NotificationManager notificationManager =
                    context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created on boot");
            }
        }
    }
}
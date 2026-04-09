package com.example.smartfinancer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GoalReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "GoalReminderReceiver";
    private static final String CHANNEL_ID = "goal_reminder_channel";
    private static final String[] SAVING_TIPS = {
            "Skip eating out today and save ₹200-300.",
            "Use public transport instead of a cab to save ₹100-150.",
            "Make coffee at home instead of buying it to save ₹50-100.",
            "Turn off lights and appliances when not in use to save on electricity bills.",
            "Consider buying groceries in bulk to save money.",
            "Look for coupons and discounts before making purchases.",
            "Avoid impulse purchases by waiting 24 hours before buying non-essential items.",
            "Cancel unused subscriptions to save monthly expenses.",
            "Use a refillable water bottle instead of buying bottled water.",
            "Plan your meals for the week to reduce food waste and save money."
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reminder received");

        // Check if this is a BOOT_COMPLETED broadcast
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted, rescheduling all reminders");
            rescheduleAllReminders(context);
            return;
        }

        try {
            String goalId = intent.getStringExtra("goalId");
            String goalName = intent.getStringExtra("goalName");
            double targetAmount = intent.getDoubleExtra("targetAmount", 0);
            double currentAmount = intent.getDoubleExtra("currentAmount", 0);
            double dailyAmount = intent.getDoubleExtra("dailyAmount", 0);
            double monthlyAmount = intent.getDoubleExtra("monthlyAmount", 0);
            String deadline = intent.getStringExtra("deadline");
            int daysLeft = intent.getIntExtra("daysLeft", 0);

            if (goalId == null || goalName == null || deadline == null) {
                Log.e(TAG, "Missing required data in intent");
                return;
            }

            // Calculate time left and amount needed
            if (daysLeft <= 0) {
                daysLeft = getDaysLeft(deadline);
            }

            double remaining = targetAmount - currentAmount;

            // Get a random saving tip
            Random random = new Random();
            String savingTip = SAVING_TIPS[random.nextInt(SAVING_TIPS.length)];

            // Format currency values
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String amountStr;
            String timeStr;

            if (daysLeft < 30) {
                // For short-term goals
                amountStr = currencyFormat.format(dailyAmount).replace("₹", "₹ ");
                timeStr = daysLeft + " days";
            } else {
                // For longer-term goals
                amountStr = currencyFormat.format(monthlyAmount).replace("₹", "₹ ");
                timeStr = (int) Math.ceil(daysLeft / 30.0) + " months";
            }

            String remainingAmountStr = currencyFormat.format(remaining).replace("₹", "₹ ");

            // Create notification content
            String title = "Goal Reminder: " + goalName;
            String message;

            if (daysLeft <= 0) {
                message = "Your deadline has passed! You still need " + remainingAmountStr +
                        " to complete your goal. Consider updating your deadline.";
            } else {
                message = "You have " + timeStr + " left to reach your goal. " +
                        "You need to save " + amountStr + " per " + (daysLeft < 30 ? "day" : "month") + ". " +
                        "Work consistently to achieve this goal!\n\n" +
                        "Tip: " + savingTip;
            }

            // Create intent to open GoalSettingActivity when notification is tapped
            Intent notificationIntent = new Intent(context, GoalSettingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    goalId.hashCode(),
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText("You need to save " + amountStr + " per " + (daysLeft < 30 ? "day" : "month"))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[] { 0, 500, 200, 500 });

            // Show notification
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                // Use a unique ID for each notification based on time to avoid overwriting
                int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
                notificationManager.notify(notificationId, builder.build());
                Log.d(TAG, "Notification sent with ID: " + notificationId);
            }

            // Reschedule for 3 hours later
            rescheduleReminder(context, intent);
        } catch (Exception e) {
            Log.e(TAG, "Error processing reminder: " + e.getMessage());
            // Still try to reschedule even if there was an error
            try {
                rescheduleReminder(context, intent);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to reschedule reminder: " + ex.getMessage());
            }
        }
    }

    private void rescheduleReminder(Context context, Intent intent) {
        try {
            String goalId = intent.getStringExtra("goalId");
            if (goalId == null) return;

            Log.d(TAG, "Rescheduling reminder for goal: " + goalId);

            // Create new pending intent for next reminder
            Intent reminderIntent = new Intent(context, GoalReminderReceiver.class);
            reminderIntent.putExtras(intent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    goalId.hashCode(),
                    reminderIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Schedule for 3 hours later
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, 3); // Next reminder after 3 hours

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }
                Log.d(TAG, "Next reminder scheduled for: " + calendar.getTime().toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error rescheduling reminder: " + e.getMessage());
        }
    }

    private int getDaysLeft(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date targetDate = sdf.parse(deadline);
            Date currentDate = new Date();

            if (targetDate == null) return 0;

            long diffInMillis = targetDate.getTime() - currentDate.getTime();
            int daysLeft = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);

            return Math.max(0, daysLeft);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing deadline: " + e.getMessage());
            return 0;
        }
    }

    private void rescheduleAllReminders(Context context) {
        // This method is called when the device reboots
        // It reschedules all active goal reminders

        try {
            // Get all active reminders from SharedPreferences
            android.content.SharedPreferences prefs = context.getSharedPreferences("GoalReminders", Context.MODE_PRIVATE);
            java.util.Map<String, ?> allPrefs = prefs.getAll();

            // Find all goal IDs with active reminders
            java.util.Set<String> goalIds = new java.util.HashSet<>();
            for (String key : allPrefs.keySet()) {
                if (key.endsWith("_active") && Boolean.TRUE.equals(allPrefs.get(key))) {
                    String goalId = key.replace("_active", "");
                    goalIds.add(goalId);
                }
            }

            // Reschedule each active reminder
            for (String goalId : goalIds) {
                String goalName = prefs.getString(goalId + "_name", "Goal");
                float targetAmount = prefs.getFloat(goalId + "_target", 0);
                float currentAmount = prefs.getFloat(goalId + "_current", 0);
                String deadline = prefs.getString(goalId + "_deadline", "");

                if (!deadline.isEmpty()) {
                    // Calculate days left
                    int daysLeft = getDaysLeft(deadline);

                    // Calculate daily and monthly amounts
                    double remaining = targetAmount - currentAmount;
                    double dailyNeeded = daysLeft > 0 ? remaining / daysLeft : remaining;
                    double monthlyNeeded = daysLeft > 0 ? (remaining / daysLeft) * 30 : remaining;

                    // Create intent for notification
                    Intent intent = new Intent(context, GoalReminderReceiver.class);
                    intent.putExtra("goalId", goalId);
                    intent.putExtra("goalName", goalName);
                    intent.putExtra("targetAmount", (double) targetAmount);
                    intent.putExtra("currentAmount", (double) currentAmount);
                    intent.putExtra("dailyAmount", dailyNeeded);
                    intent.putExtra("monthlyAmount", monthlyNeeded);
                    intent.putExtra("deadline", deadline);
                    intent.putExtra("daysLeft", daysLeft);

                    // Create unique request code based on goal ID
                    int requestCode = goalId.hashCode();

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    // Schedule reminder
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    if (alarmManager != null) {
                        // Schedule for 15 minutes after boot (to avoid too many notifications at once)
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, 15);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    pendingIntent
                            );
                        } else {
                            alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    pendingIntent
                            );
                        }

                        Log.d(TAG, "Rescheduled reminder for goal: " + goalName + " after device reboot");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error rescheduling reminders after reboot: " + e.getMessage());
        }
    }
}

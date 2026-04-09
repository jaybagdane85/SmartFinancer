package com.example.smartfinancer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyGoalSummaryReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "goal_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DailyGoalSummary", "Daily summary receiver triggered");

        try {
            // Get the current user
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                // No user logged in, try to load goals from local storage
                loadGoalsFromLocal(context);
                return;
            }

            // Fetch all goals for the user to create a summary
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Goals")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            List<GoalSettingActivity.Goal> goals = new ArrayList<>();
                            double totalDailyNeeded = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GoalSettingActivity.Goal goal = document.toObject(GoalSettingActivity.Goal.class);
                                goal.setId(document.getId());

                                if (!goal.isCompleted()) {
                                    goals.add(goal);

                                    // Calculate daily amount needed
                                    double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
                                    int daysLeft = getDaysLeft(goal.getDeadline());
                                    if (daysLeft > 0) {
                                        double dailyNeeded = remaining / daysLeft;
                                        totalDailyNeeded += dailyNeeded;
                                    } else {
                                        totalDailyNeeded += remaining; // Add full remaining amount if deadline passed
                                    }
                                }
                            }

                            if (!goals.isEmpty()) {
                                sendDailySummaryNotification(context, goals, totalDailyNeeded);
                            }
                        } else {
                            // If Firestore fetch fails, try to load from local storage
                            loadGoalsFromLocal(context);
                        }

                        // Reschedule for tomorrow
                        rescheduleDailySummary(context);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DailyGoalSummary", "Error fetching goals: " + e.getMessage());
                        // If Firestore fetch fails, try to load from local storage
                        loadGoalsFromLocal(context);
                        // Reschedule for tomorrow
                        rescheduleDailySummary(context);
                    });
        } catch (Exception e) {
            Log.e("DailyGoalSummary", "Error in onReceive: " + e.getMessage());
            // Reschedule for tomorrow even if there was an error
            rescheduleDailySummary(context);
        }
    }

    private void sendDailySummaryNotification(Context context, List<GoalSettingActivity.Goal> goals, double totalDailyNeeded) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String totalDailyStr = currencyFormat.format(totalDailyNeeded).replace("₹", "₹ ");

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Daily savings target: ").append(totalDailyStr).append("\n\n");
        messageBuilder.append("Your active goals:\n");

        for (GoalSettingActivity.Goal goal : goals) {
            double progress = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
            messageBuilder.append("• ").append(goal.getGoalName()).append(": ")
                    .append(String.format("%.1f", progress)).append("% complete\n");
        }

        messageBuilder.append("\nTip: Set aside a small amount each day to reach your goals faster!");

        // Create intent to open GoalSettingActivity when notification is tapped
        Intent notificationIntent = new Intent(context, GoalSettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                "daily_summary".hashCode(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Daily Financial Goals Summary")
                .setContentText("Your daily savings target: " + totalDailyStr)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBuilder.toString()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify("daily_summary".hashCode(), builder.build());
        }
    }

    private void loadGoalsFromLocal(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("GoalPreferences", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("SavedGoals", null);

            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<GoalSettingActivity.Goal>>() {}.getType();
                List<GoalSettingActivity.Goal> savedGoals = gson.fromJson(json, type);

                if (savedGoals != null && !savedGoals.isEmpty()) {
                    List<GoalSettingActivity.Goal> activeGoals = new ArrayList<>();
                    double totalDailyNeeded = 0;

                    for (GoalSettingActivity.Goal goal : savedGoals) {
                        if (!goal.isCompleted()) {
                            activeGoals.add(goal);

                            // Calculate daily amount needed
                            double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
                            int daysLeft = getDaysLeft(goal.getDeadline());
                            if (daysLeft > 0) {
                                double dailyNeeded = remaining / daysLeft;
                                totalDailyNeeded += dailyNeeded;
                            } else {
                                totalDailyNeeded += remaining;
                            }
                        }
                    }

                    if (!activeGoals.isEmpty()) {
                        sendDailySummaryNotification(context, activeGoals, totalDailyNeeded);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DailyGoalSummary", "Error loading goals from local storage: " + e.getMessage());
        }
    }

    private int getDaysLeft(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date targetDate = sdf.parse(deadline);
            Date currentDate = new Date();

            if (targetDate == null) return 0;

            long diffInMillis = targetDate.getTime() - currentDate.getTime();
            return Math.max(0, (int) TimeUnit.MILLISECONDS.toDays(diffInMillis));
        } catch (ParseException e) {
            return 0;
        }
    }

    private void rescheduleDailySummary(Context context) {
        // Create intent for tomorrow's summary
        Intent intent = new Intent(context, DailyGoalSummaryReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                "daily_summary".hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule for tomorrow at 9:00 AM
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            try {
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
                Log.d("DailyGoalSummary", "Daily summary scheduled for: " + calendar.getTime().toString());
            } catch (Exception e) {
                Log.e("DailyGoalSummary", "Error scheduling daily summary: " + e.getMessage());
            }
        }
    }

    private int getMonthsLeft(String deadline) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date targetDate = sdf.parse(deadline);
            java.util.Date currentDate = new java.util.Date();

            if (targetDate == null) return 0;

            long diffInMillis = targetDate.getTime() - currentDate.getTime();
            return (int) java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30;
        } catch (java.text.ParseException e) {
            return 0;
        }
    }
}

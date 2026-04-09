package com.example.smartfinancer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.smartfinancer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseUtils {

    public static void saveExpense(Context context, float amount) {
        SharedPreferences prefs = context.getSharedPreferences("Expenses", Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        float totalToday = prefs.getFloat(today, 0);
        totalToday += amount;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(today, totalToday);
        editor.apply();
    }

    public static float getTodayExpense(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Expenses", Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        return prefs.getFloat(today, 0);
    }

    public static float getDailyLimit(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Limit", Context.MODE_PRIVATE);
        return prefs.getFloat("daily_limit", 0);
    }

    public static void setDailyLimit(Context context, float limit) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Limit", Context.MODE_PRIVATE).edit();
        editor.putFloat("daily_limit", limit);
        editor.apply();
    }

    public static void showNotification(Context context, float newAmount) {
        float todayExpense = getTodayExpense(context);
        float limit = getDailyLimit(context);

        String title = "SmartFinancer Alert";
        String message = "Your daily limit is ₹" + limit + ". You've spent ₹" + todayExpense + " today.";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "SmartFinancerChannel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "SmartFinancer Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }
}

package com.example.smartfinancer;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class GoalSettingActivity extends AppCompatActivity {

    private static final String TAG = "GoalSettingActivity";
    private TextInputEditText etGoalName, etTargetAmount, etCurrentSavings, etGoalDate;
    private Button btnAddGoal;
    private ProgressBar progressBar;
    private RecyclerView rvGoals;
    private GoalAdapter goalAdapter;
    private List<Goal> goalList;
    private TextView tvActiveGoals, tvCompletedGoals, tvTotalSaved;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference goalsRef;

    private String editingGoalId = null; // For updating existing goals
    private static final String CHANNEL_ID = "goal_reminder_channel";
    private static final String PREFS_NAME = "GoalPreferences";
    private static final String GOALS_KEY = "SavedGoals";
    private static final String REMINDER_PREFS = "GoalReminders";

    // Request code for notification permission
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    // Activity result launcher for notification permission
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Notification permission granted");
                    } else {
                        Log.d(TAG, "Notification permission denied");
                        showNotificationPermissionDialog();
                    }
                }
        );

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            goalsRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Goals");
        } else {
            // Handle not logged in state
            Toast.makeText(this, "Please log in to manage goals", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Create notification channel for goal reminders
        createNotificationChannel();

        // Request notification permission
        requestNotificationPermission();

        // Initialize views
        etGoalName = findViewById(R.id.et_goal_name);
        etTargetAmount = findViewById(R.id.et_goal_amount);
        etCurrentSavings = findViewById(R.id.et_current_savings);
        etGoalDate = findViewById(R.id.et_goal_date);
        btnAddGoal = findViewById(R.id.btn_add_goal);
        progressBar = findViewById(R.id.progressBar);
        rvGoals = findViewById(R.id.rv_active_goals);
        tvActiveGoals = findViewById(R.id.tv_active_goals);
        tvCompletedGoals = findViewById(R.id.tv_completed_goals);
        tvTotalSaved = findViewById(R.id.tv_total_saved);

        // Setup RecyclerView
        goalList = new ArrayList<>();
        goalAdapter = new GoalAdapter(goalList, this::showGoalDetailsDialog);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalAdapter);

        // Setup click listeners
        btnAddGoal.setOnClickListener(v -> {
            if (editingGoalId != null) {
                updateGoalInFirestore(editingGoalId);
            } else {
                addGoalToFirestore();
            }
        });

        etGoalDate.setOnClickListener(v -> showDatePicker());

        // Fetch goals from Firestore
        fetchGoalsFromFirestore();

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_goal1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home1) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_goal1) {
                return true;
            } else if (id == R.id.nav_expenses1) {
                startActivity(new Intent(this, ExpenseActivity.class));
                return true;
            } else if (id == R.id.nav_income1) {
                startActivity(new Intent(this, IncomeActivity.class));
                return true;
            } else if (id == R.id.nav_profile1) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else {
                return false;
            }
        });

        // Reschedule all active reminders (in case app was restarted)
        rescheduleAllActiveReminders();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Notification Permission Required")
                .setMessage("Goal reminders require notification permission. Please enable notifications for this app in settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker() {
        DatePickerFragment fragment = new DatePickerFragment(date -> etGoalDate.setText(date));
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    // Fix the addGoalToFirestore method to handle errors better
    private void addGoalToFirestore() {
        String name = etGoalName.getText().toString().trim();
        String targetStr = etTargetAmount.getText().toString().trim();
        String savedStr = etCurrentSavings.getText().toString().trim();
        String deadline = etGoalDate.getText().toString().trim();

        if (name.isEmpty() || targetStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double target = Double.parseDouble(targetStr);
            double saved = savedStr.isEmpty() ? 0 : Double.parseDouble(savedStr);

            if (saved > target) {
                Toast.makeText(this, "Current savings cannot exceed target amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate the deadline format
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false); // Strict date parsing
                Date date = sdf.parse(deadline);
                if (date == null) {
                    Toast.makeText(this, "Invalid date format. Please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format. Please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return;
            }

            Goal goal = new Goal(name, target, saved, deadline);

            progressBar.setVisibility(View.VISIBLE);
            goalsRef.add(goal)
                    .addOnSuccessListener(documentReference -> {
                        String goalId = documentReference.getId();
                        Toast.makeText(this, "Goal added successfully", Toast.LENGTH_SHORT).show();

                        // Update the goal with its ID
                        goalsRef.document(goalId).update("id", goalId);
                        goal.setId(goalId);

                        // Save goal locally for persistence
                        saveGoalLocally(goal);

                        try {
                            // Schedule reminder notification
                            scheduleGoalReminder(goalId, name, target, saved, deadline);

                            // Show immediate notification
                            showGoalAddedNotification(name, target, saved, deadline);
                        } catch (Exception e) {
                            Log.e(TAG, "Error scheduling notifications: " + e.getMessage());
                            // Continue with the rest of the process even if notification scheduling fails
                        }

                        fetchGoalsFromFirestore();
                        clearInputFields();
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for amount fields", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error adding goal: " + e.getMessage());
        }
    }

    private void updateGoalInFirestore(String goalId) {
        String name = etGoalName.getText().toString().trim();
        String targetStr = etTargetAmount.getText().toString().trim();
        String savedStr = etCurrentSavings.getText().toString().trim();
        String deadline = etGoalDate.getText().toString().trim();

        if (name.isEmpty() || targetStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double target = Double.parseDouble(targetStr);
            double saved = savedStr.isEmpty() ? 0 : Double.parseDouble(savedStr);

            if (saved > target) {
                Toast.makeText(this, "Current savings cannot exceed target amount", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            goalsRef.document(goalId)
                    .update(
                            "goalName", name,
                            "targetAmount", target,
                            "currentAmount", saved,
                            "deadline", deadline
                    )
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Goal updated successfully", Toast.LENGTH_SHORT).show();

                        // Update goal locally
                        Goal updatedGoal = new Goal(name, target, saved, deadline);
                        updatedGoal.setId(goalId);
                        updateGoalLocally(updatedGoal);

                        // Update reminder notification
                        cancelGoalReminder(goalId);
                        scheduleGoalReminder(goalId, name, target, saved, deadline);

                        fetchGoalsFromFirestore();
                        clearInputFields();
                        editingGoalId = null;
                        btnAddGoal.setText("Create Goal");
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for amount fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        etGoalName.setText("");
        etTargetAmount.setText("");
        etCurrentSavings.setText("");
        etGoalDate.setText("");
    }

    private void fetchGoalsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        goalList.clear();

        goalsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double totalSaved = 0;
                int activeCount = 0;
                int completedCount = 0;
                List<Goal> fetchedGoals = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Goal goal = document.toObject(Goal.class);
                        goal.setId(document.getId());
                        goalList.add(goal);
                        fetchedGoals.add(goal);

                        totalSaved += goal.getCurrentAmount();
                        if (goal.isCompleted()) {
                            completedCount++;
                        } else {
                            activeCount++;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing goal: " + e.getMessage());
                    }
                }

                // Save all goals locally
                saveGoalsLocally(fetchedGoals);

                goalAdapter.notifyDataSetChanged();

                // Update summary
                tvActiveGoals.setText(String.valueOf(activeCount));
                tvCompletedGoals.setText(String.valueOf(completedCount));

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                String formattedTotal = currencyFormat.format(totalSaved).replace("₹", "₹");
                tvTotalSaved.setText(formattedTotal);

                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Error fetching goals", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

                // If Firestore fetch fails, load goals from local storage
                loadGoalsFromLocal();
            }
        });
    }

    private void saveGoalLocally(Goal goal) {
        List<Goal> savedGoals = getSavedGoals();

        // Check if goal already exists
        boolean exists = false;
        for (int i = 0; i < savedGoals.size(); i++) {
            if (savedGoals.get(i).getId() != null &&
                    savedGoals.get(i).getId().equals(goal.getId())) {
                savedGoals.set(i, goal); // Replace with updated goal
                exists = true;
                break;
            }
        }

        if (!exists) {
            savedGoals.add(goal);
        }

        saveGoalsLocally(savedGoals);
    }

    private void updateGoalLocally(Goal updatedGoal) {
        List<Goal> savedGoals = getSavedGoals();

        for (int i = 0; i < savedGoals.size(); i++) {
            if (savedGoals.get(i).getId() != null &&
                    savedGoals.get(i).getId().equals(updatedGoal.getId())) {
                savedGoals.set(i, updatedGoal);
                break;
            }
        }

        saveGoalsLocally(savedGoals);
    }

    private void removeGoalLocally(String goalId) {
        List<Goal> savedGoals = getSavedGoals();

        for (int i = 0; i < savedGoals.size(); i++) {
            if (savedGoals.get(i).getId() != null &&
                    savedGoals.get(i).getId().equals(goalId)) {
                savedGoals.remove(i);
                break;
            }
        }

        saveGoalsLocally(savedGoals);
    }

    private void saveGoalsLocally(List<Goal> goals) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(goals);

        editor.putString(GOALS_KEY, json);
        editor.apply();
    }

    private List<Goal> getSavedGoals() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(GOALS_KEY, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Goal>>() {}.getType();

        List<Goal> savedGoals = gson.fromJson(json, type);
        return savedGoals != null ? savedGoals : new ArrayList<>();
    }

    private void loadGoalsFromLocal() {
        List<Goal> savedGoals = getSavedGoals();

        if (!savedGoals.isEmpty()) {
            goalList.clear();
            goalList.addAll(savedGoals);
            goalAdapter.notifyDataSetChanged();

            // Update summary
            double totalSaved = 0;
            int activeCount = 0;
            int completedCount = 0;

            for (Goal goal : savedGoals) {
                totalSaved += goal.getCurrentAmount();
                if (goal.isCompleted()) {
                    completedCount++;
                } else {
                    activeCount++;
                }
            }

            tvActiveGoals.setText(String.valueOf(activeCount));
            tvCompletedGoals.setText(String.valueOf(completedCount));

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String formattedTotal = currencyFormat.format(totalSaved).replace("₹", "₹");
            tvTotalSaved.setText(formattedTotal);
        }
    }

    private void showGoalDetailsDialog(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goal_details, null);

        TextView tvGoalName = view.findViewById(R.id.tv_goal_detail_name);
        TextView tvTarget = view.findViewById(R.id.tv_goal_detail_target);
        TextView tvSaved = view.findViewById(R.id.tv_goal_detail_saved);
        TextView tvDeadline = view.findViewById(R.id.tv_goal_detail_deadline);
        TextView tvMonthlyNeeded = view.findViewById(R.id.tv_goal_detail_monthly);
        EditText etAddFunds = view.findViewById(R.id.et_add_funds);
        Button btnAddFunds = view.findViewById(R.id.btn_add_funds);
        Button btnEditGoal = view.findViewById(R.id.btn_edit_goal);
        Button btnDeleteGoal = view.findViewById(R.id.btn_delete_goal);

        // Set goal details
        tvGoalName.setText(goal.getGoalName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String targetAmount1 = currencyFormat.format(goal.getTargetAmount()).replace("₹", "₹ ");
        String savedAmount = currencyFormat.format(goal.getCurrentAmount()).replace("₹", "₹ ");

        tvTarget.setText("Target: " + targetAmount1);
        tvSaved.setText("Saved: " + savedAmount);
        tvDeadline.setText("Deadline: " + goal.getDeadline());

        // Calculate monthly savings needed
        double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
        int daysLeft = getDaysLeft(goal.getDeadline());

        // Calculate monthly and daily amounts needed
        double perDay = daysLeft > 0 ? remaining / daysLeft : remaining;
        double perMonth = daysLeft > 0 ? (remaining / daysLeft) * 30 : remaining;

        if (remaining <= 0) {
            tvMonthlyNeeded.setText("Goal completed! 🎉");
        } else if (daysLeft <= 0) {
            tvMonthlyNeeded.setText("Deadline passed! You need " +
                    currencyFormat.format(remaining).replace("₹", "₹ ") + " to complete this goal.");
        } else if (daysLeft < 30) {
            // For short-term goals (less than a month)
            tvMonthlyNeeded.setText("Save " + currencyFormat.format(perDay).replace("₹", "₹ ") +
                    " per day to reach your goal in " + daysLeft + " days.");
        } else {
            // For longer-term goals
            int monthsLeft = (int) Math.ceil(daysLeft / 30.0);
            tvMonthlyNeeded.setText("Save " + currencyFormat.format(perMonth).replace("₹", "₹ ") +
                    " per month to reach your goal in " + monthsLeft + " months.");
        }

        // Add funds button
        btnAddFunds.setOnClickListener(v -> {
            String fundStr = etAddFunds.getText().toString().trim();
            if (!fundStr.isEmpty()) {
                try {
                    double fund = Double.parseDouble(fundStr);
                    if (fund <= 0) {
                        Toast.makeText(this, "Please enter a positive amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create final copies of the values we need in the lambda
                    final String goalId = goal.getId();
                    final String goalName = goal.getGoalName();
                    final double targetAmount = goal.getTargetAmount();
                    final double currentAmount = goal.getCurrentAmount();

                    double newAmount = currentAmount + fund;
                    if (newAmount > targetAmount) {
                        newAmount = targetAmount; // Cap at target amount
                    }

                    // Final copy of newAmount for use in lambda
                    final double finalNewAmount = newAmount;

                    progressBar.setVisibility(View.VISIBLE);
                    goalsRef.document(goalId)
                            .update("currentAmount", finalNewAmount)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Funds added successfully", Toast.LENGTH_SHORT).show();

                                // Update goal locally
                                Goal updatedGoal = new Goal(goalName, targetAmount, finalNewAmount, goal.getDeadline());
                                updatedGoal.setId(goalId);
                                updateGoalLocally(updatedGoal);

                                // Check if goal is now complete
                                if (finalNewAmount >= targetAmount) {
                                    showGoalCompletedDialog(goalName);
                                    cancelGoalReminder(goalId);
                                } else {
                                    // Update reminder with new amount
                                    cancelGoalReminder(goalId);
                                    scheduleGoalReminder(goalId, goalName, targetAmount, finalNewAmount, goal.getDeadline());
                                }

                                fetchGoalsFromFirestore();
                                builder.create().dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to add funds: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            });
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit goal button
        btnEditGoal.setOnClickListener(v -> {
            etGoalName.setText(goal.getGoalName());
            etTargetAmount.setText(String.valueOf(goal.getTargetAmount()));
            etCurrentSavings.setText(String.valueOf(goal.getCurrentAmount()));
            etGoalDate.setText(goal.getDeadline());

            editingGoalId = goal.getId();
            btnAddGoal.setText("Update Goal");

            builder.create().dismiss();
        });

        // Delete goal button
        btnDeleteGoal.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Goal")
                    .setMessage("Are you sure you want to delete this goal?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        progressBar.setVisibility(View.VISIBLE);
                        goalsRef.document(goal.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Goal deleted successfully",
                                            Toast.LENGTH_SHORT).show();

                                    // Remove goal locally
                                    removeGoalLocally(goal.getId());

                                    // Cancel reminder notification
                                    cancelGoalReminder(goal.getId());

                                    fetchGoalsFromFirestore();
                                    builder.create().dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to delete goal: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        builder.setView(view);
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void showGoalCompletedDialog(String goalName) {
        new AlertDialog.Builder(this)
                .setTitle("Goal Completed! 🎉")
                .setMessage("Congratulations! You've completed your goal: " + goalName)
                .setPositiveButton("Great!", null)
                .show();
    }

    // Fix the getDaysLeft method to handle date parsing errors better
    private int getDaysLeft(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date targetDate = sdf.parse(deadline);
            Date currentDate = new Date();

            if (targetDate == null) return 0;

            // Calculate difference in milliseconds
            long diffInMillis = targetDate.getTime() - currentDate.getTime();

            // Convert to days
            int daysLeft = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);

            // Ensure we don't return negative days
            return Math.max(0, daysLeft);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return 0;
        }
    }

    private int getMonthsLeft(String deadline) {
        int daysLeft = getDaysLeft(deadline);
        // Convert days to months (approximate)
        return (int) Math.ceil(daysLeft / 30.0);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Goal Reminders";
            String description = "Notifications for financial goal reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    // Show immediate notification when a goal is added
    private void showGoalAddedNotification(String goalName, double targetAmount, double currentAmount, String deadline) {
        Intent intent = new Intent(this, GoalSettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int daysLeft = getDaysLeft(deadline);
        double remaining = targetAmount - currentAmount;

        // Calculate daily and monthly amounts
        double dailyNeeded = daysLeft > 0 ? remaining / daysLeft : remaining;
        double monthlyNeeded = daysLeft > 0 ? (remaining / daysLeft) * 30 : remaining;

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String amountStr;
        String timeStr;

        if (daysLeft < 30) {
            // For short-term goals
            amountStr = currencyFormat.format(dailyNeeded).replace("₹", "₹ ");
            timeStr = daysLeft + " days";
        } else {
            // For longer-term goals
            amountStr = currencyFormat.format(monthlyNeeded).replace("₹", "₹ ");
            timeStr = getMonthsLeft(deadline) + " months";
        }

        String title = "New Goal Added: " + goalName;
        String message = "You have " + timeStr + " to reach your goal. " +
                "You need to save " + amountStr + " per " + (daysLeft < 30 ? "day" : "month") + ". " +
                "Work consistently to achieve this goal!";

        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText("You need to save " + amountStr + " per " + (daysLeft < 30 ? "day" : "month"))
                        .setStyle(new androidx.core.app.NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 0, 500, 200, 500 });

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Goal added notification sent");
        }
    }

    // Fix the scheduleGoalReminder method to handle edge cases better
    private void scheduleGoalReminder(String goalId, String goalName, double targetAmount,
                                      double currentAmount, String deadline) {
        Log.d(TAG, "Scheduling reminder for goal: " + goalName);

        // Calculate when to send reminders
        int daysLeft = getDaysLeft(deadline);
        double remaining = targetAmount - currentAmount;

        // Calculate daily and monthly amounts with safety checks
        double dailyNeeded = 0;
        double monthlyNeeded = 0;

        if (daysLeft > 0) {
            dailyNeeded = remaining / daysLeft;
            monthlyNeeded = (remaining / daysLeft) * 30;
        } else {
            // If deadline has passed or is today, set a reasonable default
            dailyNeeded = remaining;
            monthlyNeeded = remaining;
        }

        // Create intent for notification
        Intent intent = new Intent(this, GoalReminderReceiver.class);
        intent.putExtra("goalId", goalId);
        intent.putExtra("goalName", goalName);
        intent.putExtra("targetAmount", targetAmount);
        intent.putExtra("currentAmount", currentAmount);
        intent.putExtra("dailyAmount", dailyNeeded);
        intent.putExtra("monthlyAmount", monthlyNeeded);
        intent.putExtra("deadline", deadline);
        intent.putExtra("daysLeft", daysLeft);

        // Create unique request code based on goal ID
        int requestCode = goalId.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule reminders using AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set alarm to trigger immediately for first notification
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1); // First reminder after 1 minute

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // For Android 12+, check if we can schedule exact alarms
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        // Fall back to inexact alarm if we don't have permission
                        alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                        // Prompt user to grant permission
                        Toast.makeText(this, "Please grant permission to set exact alarms for reliable reminders",
                                Toast.LENGTH_LONG).show();
                        Intent alarmIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(alarmIntent);
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
                Log.d(TAG, "First alarm scheduled for: " + calendar.getTime().toString());

                // Also schedule a daily summary alarm
                scheduleDailySummary();

                // Store goal reminder info for boot receiver
                saveReminderInfo(goalId, goalName, targetAmount, currentAmount, deadline);
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling alarm: " + e.getMessage());
                Toast.makeText(this, "Could not schedule reminder. Please check app permissions.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Add a method to schedule daily summary notifications
    private void scheduleDailySummary() {
        Intent intent = new Intent(this, DailyGoalSummaryReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                "daily_summary".hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule for tomorrow at 9:00 AM
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
                Log.d(TAG, "Daily summary scheduled for: " + calendar.getTime().toString());
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling daily summary: " + e.getMessage());
            }
        }
    }

    private void saveReminderInfo(String goalId, String goalName, double targetAmount,
                                  double currentAmount, String deadline) {
        SharedPreferences prefs = getSharedPreferences(REMINDER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(goalId + "_name", goalName);
        editor.putFloat(goalId + "_target", (float) targetAmount);
        editor.putFloat(goalId + "_current", (float) currentAmount);
        editor.putString(goalId + "_deadline", deadline);
        editor.putBoolean(goalId + "_active", true);
        editor.putLong(goalId + "_timestamp", System.currentTimeMillis());

        editor.apply();

        Log.d(TAG, "Saved reminder info for goal: " + goalId);
    }

    private void cancelGoalReminder(String goalId) {
        Log.d(TAG, "Cancelling reminder for goal: " + goalId);

        Intent intent = new Intent(this, GoalReminderReceiver.class);
        int requestCode = goalId.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Alarm cancelled for goal: " + goalId);

            // Remove reminder info
            SharedPreferences prefs = getSharedPreferences(REMINDER_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(goalId + "_active", false);
            editor.apply();
        }
    }

    private void rescheduleAllActiveReminders() {
        Log.d(TAG, "Rescheduling all active reminders");
        SharedPreferences prefs = getSharedPreferences(REMINDER_PREFS, MODE_PRIVATE);

        // Get all saved goals
        List<Goal> savedGoals = getSavedGoals();

        for (Goal goal : savedGoals) {
            String goalId = goal.getId();
            if (goalId != null && prefs.getBoolean(goalId + "_active", false)) {
                // This goal has an active reminder
                String goalName = goal.getGoalName();
                double targetAmount = goal.getTargetAmount();
                double currentAmount = goal.getCurrentAmount();
                String deadline = goal.getDeadline();

                // Reschedule the reminder
                Log.d(TAG, "Rescheduling reminder for goal: " + goalName);
                scheduleGoalReminder(goalId, goalName, targetAmount, currentAmount, deadline);
            }
        }
    }

    // Add a method to request exact alarm permission for Android 12+
    @Override
    protected void onResume() {
        super.onResume();

        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Show dialog to request permission
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("To ensure you receive goal reminders even when the app is closed, please grant permission to set exact alarms.")
                        .setPositiveButton("Grant Permission", (dialog, which) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Later", null)
                        .show();
            }
        }
    }

    // Goal model class
    public static class Goal {
        private String id;
        private String goalName;
        private double targetAmount;
        private double currentAmount;
        private String deadline;
        private boolean completed;

        public Goal() {
            // Default constructor required for Firestore
        }

        public Goal(String goalName, double targetAmount, double currentAmount, String deadline) {
            this.goalName = goalName;
            this.targetAmount = targetAmount;
            this.currentAmount = currentAmount;
            this.deadline = deadline;
            this.completed = currentAmount >= targetAmount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
        }

        public double getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(double targetAmount) {
            this.targetAmount = targetAmount;
        }

        public double getCurrentAmount() {
            return currentAmount;
        }

        public void setCurrentAmount(double currentAmount) {
            this.currentAmount = currentAmount;
            this.completed = currentAmount >= targetAmount;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public int getProgressPercentage() {
            return (int) ((currentAmount / targetAmount) * 100);
        }
    }

    // Adapter for goals
    private class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

        private List<Goal> goals;
        private final OnGoalClickListener listener;

        public GoalAdapter(List<Goal> goals, OnGoalClickListener listener) {
            this.goals = goals;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Goal goal = goals.get(position);

            holder.tvGoalName.setText(goal.getGoalName());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String targetAmount = currencyFormat.format(goal.getTargetAmount()).replace("₹", "₹ ");
            String savedAmount = currencyFormat.format(goal.getCurrentAmount()).replace("₹", "₹ ");

            holder.tvTargetAmount.setText("of " + targetAmount);
            holder.tvSavedAmount.setText(savedAmount);
            holder.tvGoalDeadline.setText("Deadline: " + goal.getDeadline());

            int progress = goal.getProgressPercentage();
            holder.progressBar.setProgress(progress);
            holder.tvProgressPercent.setText(progress + "% completed");

            holder.itemView.setOnClickListener(v -> listener.onGoalClick(goal));
        }

        @Override
        public int getItemCount() {
            return goals.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvGoalName, tvGoalDeadline, tvSavedAmount, tvTargetAmount, tvProgressPercent;
            ProgressBar progressBar;

            ViewHolder(View itemView) {
                super(itemView);
                tvGoalName = itemView.findViewById(R.id.tv_goal_name);
                tvGoalDeadline = itemView.findViewById(R.id.tv_goal_deadline);
                tvSavedAmount = itemView.findViewById(R.id.tv_saved_amount);
                tvTargetAmount = itemView.findViewById(R.id.tv_target_amount);
                tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
                progressBar = itemView.findViewById(R.id.progress_bar);
            }
        }
    }

    interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }
}

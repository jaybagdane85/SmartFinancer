package com.example.smartfinancer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private TextView tvTotalIncome, tvTotalExpense, tvNetSavings;
    private Button btnEditProfile, btnLogout;
    private LinearLayout layoutNotificationSettings, layoutSecurity, layoutHelp;
    private BottomNavigationView bottomNavigationView;

    // Update the onCreate method to use UserManager
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvNetSavings = findViewById(R.id.tvNetSavings);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        layoutNotificationSettings = findViewById(R.id.layoutNotificationSettings);
        layoutSecurity = findViewById(R.id.layoutSecurity);
        layoutHelp = findViewById(R.id.layoutHelp);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup bottom navigation
        setupBottomNavigation();

        // Get UserManager instance
        UserManager userManager = UserManager.getInstance(this);

        // Load user data from UserManager
        loadUserData(userManager);

        // Setup click listeners
        btnEditProfile.setOnClickListener(v -> {
            showEditProfileDialog(userManager);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear user session data
            FirebaseAuth.getInstance().signOut();
            userManager.clearUserData();

            // Navigate to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        layoutNotificationSettings.setOnClickListener(v -> {
            showNotificationSettingsDialog();
        });

        layoutSecurity.setOnClickListener(v -> {
            showSecuritySettingsDialog();
        });

        layoutHelp.setOnClickListener(v -> {
            showHelpAndSupportDialog();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile1);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_income1) {
                startActivity(new Intent(this, IncomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_expenses1) {
                startActivity(new Intent(this, ExpenseActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_home1) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_goal1) {
                startActivity(new Intent(this, GoalSettingActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile1) {
                return true;
            }

            return false;
        });
    }


    private void loadUserData(UserManager userManager) {
        // Set user data to views
        String userName = userManager.getDisplayName();
        String userEmail = userManager.getEmail();
        String userPhone = userManager.getPhoneNumber();

        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("User");
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            tvUserEmail.setText(userEmail);
        } else {
            tvUserEmail.setText("No email provided");
        }

        if (userPhone != null && !userPhone.isEmpty()) {
            tvUserPhone.setText(userPhone);
        } else {
            tvUserPhone.setText("No phone number provided");
        }

        // Load financial summary
        double monthlyIncome = userManager.getMonthlyIncome();
        double monthlySavingsGoal = userManager.getMonthlySavingsGoal();

        // For demo purposes, we'll calculate expenses as income minus savings goal
        double monthlyExpense = monthlyIncome - monthlySavingsGoal;
        if (monthlyExpense < 0) monthlyExpense = 0;

        double netSavings = monthlyIncome - monthlyExpense;

        tvTotalIncome.setText("₹" + String.format("%,.0f", monthlyIncome));
        tvTotalExpense.setText("₹" + String.format("%,.0f", monthlyExpense));
        tvNetSavings.setText("₹" + String.format("%,.0f", netSavings));
    }

    private void showEditProfileDialog(UserManager userManager) {
        // Create dialog for editing profile
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etMonthlyIncome = view.findViewById(R.id.etMonthlyIncome);
        EditText etSavingsGoal = view.findViewById(R.id.etSavingsGoal);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Pre-fill with current values
        etName.setText(userManager.getDisplayName());
        etPhone.setText(userManager.getPhoneNumber());
        etMonthlyIncome.setText(String.valueOf(userManager.getMonthlyIncome()));
        etSavingsGoal.setText(String.valueOf(userManager.getMonthlySavingsGoal()));

        builder.setView(view);
        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String incomeStr = etMonthlyIncome.getText().toString().trim();
            String savingsStr = etSavingsGoal.getText().toString().trim();

            double income = 0;
            double savings = 0;

            try {
                if (!incomeStr.isEmpty()) income = Double.parseDouble(incomeStr);
                if (!savingsStr.isEmpty()) savings = Double.parseDouble(savingsStr);

                userManager.updateUserProfile(name, phone, income, savings, new UserManager.OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        loadUserData(userManager);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProfileActivity.this, "Update failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers for income and savings goal", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showNotificationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_notification_settings, null);

        // Setup notification settings UI elements here

        builder.setView(view);
        builder.setTitle("Notification Settings");
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Save notification settings
            Toast.makeText(this, "Notification settings saved", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSecuritySettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Settings");
        builder.setItems(new String[]{"Change Password", "Enable Biometric Login", "Privacy Settings"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Change password
                            Toast.makeText(this, "Change Password selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            // Enable biometric login
                            Toast.makeText(this, "Biometric Login selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            // Privacy settings
                            Toast.makeText(this, "Privacy Settings selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
        builder.show();
    }

    private void showHelpAndSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help & Support");
        builder.setMessage("For any assistance or queries, please contact us at:\n\nsupport@smartfinancer.com\n\nOr call our helpline:\n+91 9876543210");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

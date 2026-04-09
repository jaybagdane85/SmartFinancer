package com.example.smartfinancer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private TextView tvTotalIncome, tvTotalExpense, tvNetSavings;
    private Button btnEditProfile, btnLogout;
    private LinearLayout layoutNotificationSettings, layoutSecurity, layoutHelp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvNetSavings = view.findViewById(R.id.tv_net_savings);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        layoutNotificationSettings = view.findViewById(R.id.layout_notification_settings);
        layoutSecurity = view.findViewById(R.id.layout_security);
        layoutHelp = view.findViewById(R.id.layout_help);

        // Load user data
        loadUserData();

        // Setup click listeners
        btnEditProfile.setOnClickListener(v -> {
            // Show edit profile dialog or navigate to edit profile screen
            Toast.makeText(getContext(), "Edit Profile (To be implemented)",
                    Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            // Clear user session data
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Navigate to LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        layoutNotificationSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notification Settings (To be implemented)",
                    Toast.LENGTH_SHORT).show();
        });

        layoutSecurity.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Security Settings (To be implemented)",
                    Toast.LENGTH_SHORT).show();
        });

        layoutHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help & Support (To be implemented)",
                    Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadUserData() {
        // Load user data from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("userName", "John Doe");
        String userEmail = prefs.getString("userEmail", "john.doe@example.com");
        String userPhone = prefs.getString("userPhone", "+91 9876543210");

        // Set user data to views
        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);
        tvUserPhone.setText(userPhone);

        // Load financial summary (this would typically come from a database)
        // For demo purposes, we're using hardcoded values
        double totalIncome = 45000;
        double totalExpense = 32000;
        double netSavings = totalIncome - totalExpense;

        tvTotalIncome.setText("₹" + String.format("%,.0f", totalIncome));
        tvTotalExpense.setText("₹" + String.format("%,.0f", totalExpense));
        tvNetSavings.setText("₹" + String.format("%,.0f", netSavings));
    }
}


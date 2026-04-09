package com.example.smartfinancer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage user data across the app
 */
public class UserManager {
    private static final String TAG = "UserManager";
    private static final String PREFS_NAME = "UserPrefs";

    private static UserManager instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final SharedPreferences prefs;

    // User data
    private String userId;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private double monthlyIncome;
    private double monthlySavingsGoal;

    private UserManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load cached user data
        loadUserDataFromPrefs();

        // Check if user is logged in and refresh data
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            refreshUserData();
        }
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Load user data from SharedPreferences (cached)
     */
    private void loadUserDataFromPrefs() {
        userId = prefs.getString("userId", null);
        displayName = prefs.getString("displayName", null);
        email = prefs.getString("email", null);
        phoneNumber = prefs.getString("phoneNumber", null);
        profileImageUrl = prefs.getString("profileImageUrl", null);
        monthlyIncome = prefs.getFloat("monthlyIncome", 0);
        monthlySavingsGoal = prefs.getFloat("monthlySavingsGoal", 0);
    }

    /**
     * Save user data to SharedPreferences (cache)
     */
    private void saveUserDataToPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("displayName", displayName);
        editor.putString("email", email);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("profileImageUrl", profileImageUrl);
        editor.putFloat("monthlyIncome", (float) monthlyIncome);
        editor.putFloat("monthlySavingsGoal", (float) monthlySavingsGoal);
        editor.apply();
    }

    /**
     * Refresh user data from Firestore
     */
    public void refreshUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userId = user.getUid();
        email = user.getEmail();
        displayName = user.getDisplayName();
        phoneNumber = user.getPhoneNumber();

        if (user.getPhotoUrl() != null) {
            profileImageUrl = user.getPhotoUrl().toString();
        }

        // Get additional user data from Firestore
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Update user data from Firestore
                    if (document.contains("displayName") && displayName == null) {
                        displayName = document.getString("displayName");
                    }

                    if (document.contains("phoneNumber") && phoneNumber == null) {
                        phoneNumber = document.getString("phoneNumber");
                    }

                    if (document.contains("profileImageUrl")) {
                        profileImageUrl = document.getString("profileImageUrl");
                    }

                    if (document.contains("monthlyIncome")) {
                        monthlyIncome = document.getDouble("monthlyIncome");
                    }

                    if (document.contains("monthlySavingsGoal")) {
                        monthlySavingsGoal = document.getDouble("monthlySavingsGoal");
                    }

                    // Save updated data to prefs
                    saveUserDataToPrefs();
                } else {
                    // Document doesn't exist, create it with current data
                    createUserDocument();
                }
            } else {
                Log.w(TAG, "Error getting user document", task.getException());
            }
        });
    }

    /**
     * Create a new user document in Firestore
     */
    private void createUserDocument() {
        if (userId == null) return;

        Map<String, Object> userData = new HashMap<>();
        if (displayName != null) userData.put("displayName", displayName);
        if (email != null) userData.put("email", email);
        if (phoneNumber != null) userData.put("phoneNumber", phoneNumber);
        if (profileImageUrl != null) userData.put("profileImageUrl", profileImageUrl);
        userData.put("monthlyIncome", monthlyIncome);
        userData.put("monthlySavingsGoal", monthlySavingsGoal);
        userData.put("createdAt", new java.util.Date());

        db.collection("Users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document created"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating user document", e));
    }

    /**
     * Update user profile information
     */
    public void updateUserProfile(String newDisplayName, String newPhoneNumber,
                                  double newMonthlyIncome, double newMonthlySavingsGoal,
                                  OnUserUpdateListener listener) {
        if (userId == null) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        // Update Firebase Auth profile
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && newDisplayName != null && !newDisplayName.isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated in Auth");
                        }
                    });
        }

        // Update Firestore document
        Map<String, Object> updates = new HashMap<>();
        if (newDisplayName != null && !newDisplayName.isEmpty()) {
            updates.put("displayName", newDisplayName);
            displayName = newDisplayName;
        }

        if (newPhoneNumber != null && !newPhoneNumber.isEmpty()) {
            updates.put("phoneNumber", newPhoneNumber);
            phoneNumber = newPhoneNumber;
        }

        updates.put("monthlyIncome", newMonthlyIncome);
        updates.put("monthlySavingsGoal", newMonthlySavingsGoal);
        updates.put("updatedAt", new java.util.Date());

        monthlyIncome = newMonthlyIncome;
        monthlySavingsGoal = newMonthlySavingsGoal;

        db.collection("Users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document updated");
                    saveUserDataToPrefs();
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating user document", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Update user profile image
     */
    public void updateProfileImage(Uri imageUri, OnUserUpdateListener listener) {
        if (userId == null || imageUri == null) {
            if (listener != null) listener.onFailure("User not logged in or invalid image");
            return;
        }

        // Here you would typically upload the image to Firebase Storage
        // For simplicity, we'll just update the URL directly

        // Update Firebase Auth profile
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(imageUri)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile image updated in Auth");

                            // Update Firestore
                            profileImageUrl = imageUri.toString();
                            db.collection("Users").document(userId)
                                    .update("profileImageUrl", profileImageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Profile image URL updated in Firestore");
                                        saveUserDataToPrefs();
                                        if (listener != null) listener.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating profile image URL", e);
                                        if (listener != null) listener.onFailure(e.getMessage());
                                    });
                        } else {
                            if (listener != null) listener.onFailure("Failed to update profile image");
                        }
                    });
        }
    }

    /**
     * Clear user data on logout
     */
    public void clearUserData() {
        userId = null;
        displayName = null;
        email = null;
        phoneNumber = null;
        profileImageUrl = null;
        monthlyIncome = 0;
        monthlySavingsGoal = 0;

        // Clear shared preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public double getMonthlySavingsGoal() {
        return monthlySavingsGoal;
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    // Callback interface
    public interface OnUserUpdateListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}

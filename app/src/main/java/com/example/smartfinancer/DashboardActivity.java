package com.example.smartfinancer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView;
    private TextView tvWelcomeUser;

    // Investment cards
    private CardView cardBestSIP, cardMutualFunds, cardGold, cardLargeCapFunds;

    // Loan cards
    private CardView cardPersonalLoan, cardGoldLoan;

    // Insurance cards
    private CardView cardTermLife, cardHealth;

    // Payment options cards
    private CardView cardScanQR, cardToMobile;

    // Monthly payouts cards
    private CardView cardBills, cardRecharges;

    // Money cards
    private CardView cardMoneyGiven, cardMoneyTaken;

    // Rents & Subscription cards
    private CardView cardRents, cardSubscription;

    // Notification card
    private CardView cardNotification;

    // Chatbot
    private FloatingActionButton fabChatbot;
    private ChatbotDialog chatbotDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Initialize bottom navigation
        setupBottomNavigation();

        // Initialize chatbot
        chatbotDialog = new ChatbotDialog(this);
    }

    private void initViews() {
        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // TextViews
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);

        // Investment cards
        cardBestSIP = findViewById(R.id.cardBestSIP);
        cardMutualFunds = findViewById(R.id.cardMutualFunds);
        cardGold = findViewById(R.id.cardGold);
        cardLargeCapFunds = findViewById(R.id.cardLargeCapFunds);

        // Loan cards
        cardPersonalLoan = findViewById(R.id.cardPersonalLoan);
        cardGoldLoan = findViewById(R.id.cardGoldLoan);

        // Insurance cards
        cardTermLife = findViewById(R.id.cardTermLife);
        cardHealth = findViewById(R.id.cardHealth);

        // Payment options cards
        cardScanQR = findViewById(R.id.cardScanQR);
        cardToMobile = findViewById(R.id.cardToMobile);

        // Monthly payouts cards
        cardBills = findViewById(R.id.cardBills);
        cardRecharges = findViewById(R.id.cardRecharges);

        // Money cards
        cardMoneyGiven = findViewById(R.id.cardMoneyGiven);
        cardMoneyTaken = findViewById(R.id.cardMoneyTaken);

        // Rents & Subscription cards
        cardRents = findViewById(R.id.cardRents);
        cardSubscription = findViewById(R.id.cardSubscription);

        // Notification card
        cardNotification = findViewById(R.id.cardNotification);

        // Chatbot
        fabChatbot = findViewById(R.id.fab_chatbot);
    }

    private void setupClickListeners() {
        // Investment cards
        cardBestSIP.setOnClickListener(this);
        cardMutualFunds.setOnClickListener(this);
        cardGold.setOnClickListener(this);
        cardLargeCapFunds.setOnClickListener(this);

        // Loan cards
        cardPersonalLoan.setOnClickListener(this);
        cardGoldLoan.setOnClickListener(this);

        // Insurance cards
        cardTermLife.setOnClickListener(this);
        cardHealth.setOnClickListener(this);

        // Payment options cards
        cardScanQR.setOnClickListener(this);
        cardToMobile.setOnClickListener(this);

        // Monthly payouts cards
        cardBills.setOnClickListener(this);
        cardRecharges.setOnClickListener(this);

        // Money cards
        cardMoneyGiven.setOnClickListener(this);
        cardMoneyTaken.setOnClickListener(this);

        // Rents & Subscription cards
        cardRents.setOnClickListener(this);
        cardSubscription.setOnClickListener(this);

        // Notification card
        cardNotification.setOnClickListener(this);

        // Chatbot
        fabChatbot.setOnClickListener(v -> chatbotDialog.show());
    }

    private void setupBottomNavigation() {
        // Set Default Selected Item
        bottomNavigationView.setSelectedItemId(R.id.nav_home1);

        // Handle Bottom Navigation Item Selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home1) {
                    // Stay on the current activity
                    return true;
                } else if (itemId == R.id.nav_income1) {
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_expenses1) {
                    startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_goal1) {
                    startActivity(new Intent(getApplicationContext(), GoalSettingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile1) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        // Investment section
        if (id == R.id.cardBestSIP) {
            Toast.makeText(this, "Best SIP Funds", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BestSIPActivity.class));
        } else if (id == R.id.cardMutualFunds) {
            Toast.makeText(this, "Mutual Funds", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MutualFundsActivity.class));
        } else if (id == R.id.cardGold) {
            Toast.makeText(this, "Gold Investment", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, GoldInvestmentActivity.class));
        } else if (id == R.id.cardLargeCapFunds) {
            Toast.makeText(this, "Large CAP Funds", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LargeCapFundsActivity.class));
        }
        // Loan section
        else if (id == R.id.cardPersonalLoan) {
            Toast.makeText(this, "Personal Loan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PersonalLoanActivity.class));
        } else if (id == R.id.cardGoldLoan) {
            Toast.makeText(this, "Gold Loan", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, GoldLoanActivity.class));
        }
        // Insurance section
        else if (id == R.id.cardTermLife) {
            Toast.makeText(this, "Term Life Insurance", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, TermLifeActivity.class));
        } else if (id == R.id.cardHealth) {
            Toast.makeText(this, "Health Insurance", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HealthInsuranceActivity.class));
        }
        // Payment options section
        else if (id == R.id.cardScanQR) {
            Toast.makeText(this, "Scan QR Code", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, QRScannerActivity.class));
        } else if (id == R.id.cardToMobile) {
            Toast.makeText(this, "Pay to Mobile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MobilePaymentActivity.class));
        }
        // Monthly payouts section
        else if (id == R.id.cardBills) {
            Toast.makeText(this, "Bills", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BillsActivity.class));
        } else if (id == R.id.cardRecharges) {
            Toast.makeText(this, "Recharges", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RechargesActivity.class));
        }
        // Money section
        else if (id == R.id.cardMoneyGiven) {
            Toast.makeText(this, "Money Given", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MoneyGivenActivity.class));
        } else if (id == R.id.cardMoneyTaken) {
            Toast.makeText(this, "Money Taken", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MoneyTakenActivity.class));
        }
        // Rents & Subscription section
        else if (id == R.id.cardRents) {
            Toast.makeText(this, "Rents", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RentsActivity.class));
        } else if (id == R.id.cardSubscription) {
            Toast.makeText(this, "Subscriptions", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SubscriptionActivity.class));
        }
        // Notification
        else if (id == R.id.cardNotification) {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NotificationActivity.class));
        }
    }
}

package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTotalSubscriptions, tvActiveCount, tvNextDue;
    private Button btnAddNew, btnManage1, btnManage2, btnManage3, btnViewAll, btnOptimize;
    private CardView cardSubscription1, cardSubscription2, cardSubscription3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Summary views
        tvTotalSubscriptions = findViewById(R.id.tvTotalSubscriptions);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvNextDue = findViewById(R.id.tvNextDue);

        // Buttons
        btnAddNew = findViewById(R.id.btnAddNew);
        btnManage1 = findViewById(R.id.btnManage1);
        btnManage2 = findViewById(R.id.btnManage2);
        btnManage3 = findViewById(R.id.btnManage3);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnOptimize = findViewById(R.id.btnOptimize);

        // Subscription cards
        cardSubscription1 = findViewById(R.id.cardSubscription1);
        cardSubscription2 = findViewById(R.id.cardSubscription2);
        cardSubscription3 = findViewById(R.id.cardSubscription3);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Buttons
        btnAddNew.setOnClickListener(this);
        btnManage1.setOnClickListener(this);
        btnManage2.setOnClickListener(this);
        btnManage3.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        btnOptimize.setOnClickListener(this);

        // Subscription cards
        cardSubscription1.setOnClickListener(this);
        cardSubscription2.setOnClickListener(this);
        cardSubscription3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Buttons
        else if (id == R.id.btnAddNew) {
            Toast.makeText(this, "Add new subscription", Toast.LENGTH_SHORT).show();
            // Show add new subscription dialog
        } else if (id == R.id.btnManage1) {
            Toast.makeText(this, "Manage Netflix subscription", Toast.LENGTH_SHORT).show();
            // Show Netflix subscription management options
        } else if (id == R.id.btnManage2) {
            Toast.makeText(this, "Manage Spotify subscription", Toast.LENGTH_SHORT).show();
            // Show Spotify subscription management options
        } else if (id == R.id.btnManage3) {
            Toast.makeText(this, "Manage Amazon Prime subscription", Toast.LENGTH_SHORT).show();
            // Show Amazon Prime subscription management options
        } else if (id == R.id.btnViewAll) {
            Toast.makeText(this, "View all subscriptions", Toast.LENGTH_SHORT).show();
            // Show all subscriptions
        } else if (id == R.id.btnOptimize) {
            Toast.makeText(this, "Optimize subscriptions", Toast.LENGTH_SHORT).show();
            // Show subscription optimization suggestions
        }
        // Subscription cards
        else if (id == R.id.cardSubscription1) {
            Toast.makeText(this, "Netflix subscription details", Toast.LENGTH_SHORT).show();
            // Show Netflix subscription details
        } else if (id == R.id.cardSubscription2) {
            Toast.makeText(this, "Spotify subscription details", Toast.LENGTH_SHORT).show();
            // Show Spotify subscription details
        } else if (id == R.id.cardSubscription3) {
            Toast.makeText(this, "Amazon Prime subscription details", Toast.LENGTH_SHORT).show();
            // Show Amazon Prime subscription details
        }
    }
}
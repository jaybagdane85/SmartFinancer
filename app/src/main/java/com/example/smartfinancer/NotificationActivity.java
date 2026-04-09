package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private Button btnMarkAllRead, btnViewAll, btnNotificationSettings;
    private CardView cardNotification1, cardNotification2, cardNotification3, cardNotification4, cardNotification5;
    private View viewUnread1, viewUnread2, viewUnread3, viewUnread4, viewUnread5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Buttons
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        // Notification cards
        cardNotification1 = findViewById(R.id.cardNotification1);
        cardNotification2 = findViewById(R.id.cardNotification2);
        cardNotification3 = findViewById(R.id.cardNotification3);
        cardNotification4 = findViewById(R.id.cardNotification4);
        cardNotification5 = findViewById(R.id.cardNotification5);

        // Unread indicators
        viewUnread1 = findViewById(R.id.viewUnread1);
        viewUnread2 = findViewById(R.id.viewUnread2);
        viewUnread3 = findViewById(R.id.viewUnread3);
        viewUnread4 = findViewById(R.id.viewUnread4);
        viewUnread5 = findViewById(R.id.viewUnread5);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Buttons
        btnMarkAllRead.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        btnNotificationSettings.setOnClickListener(this);

        // Notification cards
        cardNotification1.setOnClickListener(this);
        cardNotification2.setOnClickListener(this);
        cardNotification3.setOnClickListener(this);
        cardNotification4.setOnClickListener(this);
        cardNotification5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Buttons
        else if (id == R.id.btnMarkAllRead) {
            markAllAsRead();
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnViewAll) {
            Toast.makeText(this, "Viewing all notifications", Toast.LENGTH_SHORT).show();
            // Show all notifications
        } else if (id == R.id.btnNotificationSettings) {
            Toast.makeText(this, "Opening notification settings", Toast.LENGTH_SHORT).show();
            // Open notification settings
        }
        // Notification cards
        else if (id == R.id.cardNotification1) {
            markAsRead(viewUnread1);
            Toast.makeText(this, "Viewing bill payment notification", Toast.LENGTH_SHORT).show();
            // Show bill payment notification details
        } else if (id == R.id.cardNotification2) {
            markAsRead(viewUnread2);
            Toast.makeText(this, "Viewing money received notification", Toast.LENGTH_SHORT).show();
            // Show money received notification details
        } else if (id == R.id.cardNotification3) {
            Toast.makeText(this, "Viewing subscription renewal notification", Toast.LENGTH_SHORT).show();
            // Show subscription renewal notification details
        } else if (id == R.id.cardNotification4) {
            Toast.makeText(this, "Viewing payment reminder notification", Toast.LENGTH_SHORT).show();
            // Show payment reminder notification details
        } else if (id == R.id.cardNotification5) {
            Toast.makeText(this, "Viewing monthly report notification", Toast.LENGTH_SHORT).show();
            // Show monthly report notification details
        }
    }

    private void markAsRead(View unreadIndicator) {
        if (unreadIndicator != null) {
            unreadIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void markAllAsRead() {
        markAsRead(viewUnread1);
        markAsRead(viewUnread2);
        markAsRead(viewUnread3);
        markAsRead(viewUnread4);
        markAsRead(viewUnread5);
    }
}
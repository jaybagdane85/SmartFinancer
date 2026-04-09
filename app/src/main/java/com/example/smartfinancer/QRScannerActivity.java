package com.example.smartfinancer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack, btnFlash, btnLogout;
    private Button btnScan, btnViewAllScans;
    private ImageView previewView;
    private LinearLayout layoutRecentScans;

    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFlash = findViewById(R.id.btnFlash);
        btnLogout = findViewById(R.id.btnLogout);
        btnScan = findViewById(R.id.btnScan);
        btnViewAllScans = findViewById(R.id.btnViewAllScans);
        previewView = findViewById(R.id.previewView);
        layoutRecentScans = findViewById(R.id.layoutRecentScans);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnViewAllScans.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnFlash) {
            toggleFlash();
        } else if (id == R.id.btnLogout) {
            logoutUser();
        } else if (id == R.id.btnScan) {
            startQRScanner();
        } else if (id == R.id.btnViewAllScans) {
            Toast.makeText(this, "Viewing all scan history", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        if (isFlashOn) {
            btnFlash.setImageResource(android.R.drawable.ic_menu_view);
            Toast.makeText(this, "Flash turned on", Toast.LENGTH_SHORT).show();
        } else {
            btnFlash.setImageResource(android.R.drawable.ic_menu_compass);
            Toast.makeText(this, "Flash turned off", Toast.LENGTH_SHORT).show();
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Align QR code within the frame");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                handleQRCode(result.getContents());
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleQRCode(String qrCodeData) {
        // Assuming QR code contains a UPI link
        if (qrCodeData.startsWith("upi://")) {
            openUPIApp(qrCodeData);
        } else {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUPIApp(String upiUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(upiUri));
            Intent chooser = Intent.createChooser(intent, "Choose Payment App");
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutUser() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        // Redirect to login screen
        Intent intent = new Intent(QRScannerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

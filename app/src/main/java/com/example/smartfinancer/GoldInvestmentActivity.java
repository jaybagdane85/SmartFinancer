package com.example.smartfinancer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoldInvestmentActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tv24KGoldPrice, tv24KGoldChange, tv22KGoldPrice, tv22KGoldChange;
    private Button btnBuyDigitalGold, btnInvestGoldETF, btnInvestSGB, btnViewFullHistory;
    private CardView cardDigitalGold, cardGoldETF, cardSGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold_investment);

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Load gold prices
        loadGoldPrices();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        // Gold price views
        tv24KGoldPrice = findViewById(R.id.tv24KGoldPrice);
        tv24KGoldChange = findViewById(R.id.tv24KGoldChange);
        tv22KGoldPrice = findViewById(R.id.tv22KGoldPrice);
        tv22KGoldChange = findViewById(R.id.tv22KGoldChange);

        // Investment option cards
        cardDigitalGold = findViewById(R.id.cardDigitalGold);
        cardGoldETF = findViewById(R.id.cardGoldETF);
        cardSGB = findViewById(R.id.cardSGB);

        // Buttons
        btnBuyDigitalGold = findViewById(R.id.btnBuyDigitalGold);
        btnInvestGoldETF = findViewById(R.id.btnInvestGoldETF);
        btnInvestSGB = findViewById(R.id.btnInvestSGB);
        btnViewFullHistory = findViewById(R.id.btnViewFullHistory);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(this);

        // Investment option cards
        cardDigitalGold.setOnClickListener(this);
        cardGoldETF.setOnClickListener(this);
        cardSGB.setOnClickListener(this);

        // Buttons
        btnBuyDigitalGold.setOnClickListener(this);
        btnInvestGoldETF.setOnClickListener(this);
        btnInvestSGB.setOnClickListener(this);
        btnViewFullHistory.setOnClickListener(this);
    }

    private void loadGoldPrices() {
        // In a real app, this would fetch live gold prices from an API
        // For now, we'll use dummy data
        tv24KGoldPrice.setText("₹6,250");
        tv24KGoldChange.setText("+1.2% today");
        tv22KGoldPrice.setText("₹5,750");
        tv22KGoldChange.setText("+1.1% today");

        // Set the current date and time for last updated
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        // In a real app, you would set this to the actual last updated time from the API
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBack) {
            finish();
        }
        // Investment option cards
        else if (id == R.id.cardDigitalGold || id == R.id.btnBuyDigitalGold) {
            Toast.makeText(this, "Buy Digital Gold", Toast.LENGTH_SHORT).show();
            // Open digital gold purchase flow
        } else if (id == R.id.cardGoldETF || id == R.id.btnInvestGoldETF) {
            Toast.makeText(this, "Invest in Gold ETF", Toast.LENGTH_SHORT).show();
            // Open Gold ETF investment flow
        } else if (id == R.id.cardSGB || id == R.id.btnInvestSGB) {
            Toast.makeText(this, "Invest in Sovereign Gold Bond", Toast.LENGTH_SHORT).show();
            // Open SGB investment flow
        }
        // Other buttons
        else if (id == R.id.btnViewFullHistory) {
            Toast.makeText(this, "View Full Gold Price History", Toast.LENGTH_SHORT).show();
            // Open gold price history screen
        }
    }
}
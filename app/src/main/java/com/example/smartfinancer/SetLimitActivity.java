package com.example.smartfinancer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetLimitActivity extends AppCompatActivity {

    EditText limitInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);

        limitInput = findViewById(R.id.limit_input);
        saveBtn = findViewById(R.id.save_limit);

        saveBtn.setOnClickListener(v -> {
            int limit = Integer.parseInt(limitInput.getText().toString());
            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit().putInt("daily_limit", limit).apply();
            Toast.makeText(this, "Limit Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

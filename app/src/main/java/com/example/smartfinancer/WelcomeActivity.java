package com.example.smartfinancer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        CircleIndicator3 indicator = findViewById(R.id.indicator);

        WelcomePagerAdapter adapter = new WelcomePagerAdapter(this);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }
}

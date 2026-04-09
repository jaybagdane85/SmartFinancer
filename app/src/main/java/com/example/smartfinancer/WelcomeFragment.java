package com.example.smartfinancer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeFragment extends Fragment {

    private int imageResId;
    private String title;
    private String description;
    private boolean isLastPage;

    public WelcomeFragment(int imageResId, String title, String description, boolean isLastPage) {
        this.imageResId = imageResId;
        this.title = title;
        this.description = description;
        this.isLastPage = isLastPage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_welcome_fragment, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleText = view.findViewById(R.id.titleText);
        TextView descriptionText = view.findViewById(R.id.descriptionText);
        Button btnGetStarted = view.findViewById(R.id.btnGetStarted);

        imageView.setImageResource(imageResId);
        titleText.setText(title);
        descriptionText.setText(description);

        // Show "Get Started" button on last page
        if (isLastPage) {
            btnGetStarted.setVisibility(View.VISIBLE);
            btnGetStarted.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish(); // Close welcome screen
            });
        }

        return view;
    }
}

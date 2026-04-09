package com.example.smartfinancer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WelcomePagerAdapter extends FragmentStateAdapter {

    private final Fragment[] fragments;

    public WelcomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new Fragment[]{
                new WelcomeFragment(R.drawable.ic_welcome1, "Track Expenses", "Track all your expenses in one place.", false),
                new WelcomeFragment(R.drawable.ic_welcome2, "Set Goals", "Set and reach your financial goals.", false),
                new WelcomeFragment(R.drawable.ic_welcome3, "Manage Finances", "Manage and grow your savings.", true) // Last page
        };
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}

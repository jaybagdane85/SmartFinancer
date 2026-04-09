package com.example.smartfinancer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private final List<Goal> goals;
    private final GoalClickListener listener;

    public interface GoalClickListener {
        void onGoalClicked(Goal goal);
    }

    public GoalAdapter(List<Goal> goals, GoalClickListener listener) {
        this.goals = goals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);

        holder.tvGoalName.setText(goal.getGoalName());
        holder.tvGoalDeadline.setText("Deadline: " + goal.getDeadline());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String savedAmount = currencyFormat.format(goal.getCurrentAmount()).replace("₹", "₹ ");
        String targetAmount = currencyFormat.format(goal.getTargetAmount()).replace("₹", "₹ ");

        holder.tvSavedAmount.setText(savedAmount);
        holder.tvTargetAmount.setText("of " + targetAmount);

        int progress = goal.getProgressPercentage();
        holder.progressBar.setProgress(progress);
        holder.tvProgressPercent.setText(progress + "% completed");

        holder.btnAddFunds.setOnClickListener(v -> listener.onGoalClicked(goal));
        holder.btnGoalOptions.setOnClickListener(v -> listener.onGoalClicked(goal));

        holder.itemView.setOnClickListener(v -> listener.onGoalClicked(goal));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName, tvGoalDeadline, tvSavedAmount, tvTargetAmount, tvProgressPercent;
        ProgressBar progressBar;
        Button btnAddFunds;
        ImageButton btnGoalOptions;

        GoalViewHolder(View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvGoalDeadline = itemView.findViewById(R.id.tv_goal_deadline);
            tvSavedAmount = itemView.findViewById(R.id.tv_saved_amount);
            tvTargetAmount = itemView.findViewById(R.id.tv_target_amount);
            tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnAddFunds = itemView.findViewById(R.id.btn_add_funds);
            btnGoalOptions = itemView.findViewById(R.id.btn_goal_options);
        }
    }
}

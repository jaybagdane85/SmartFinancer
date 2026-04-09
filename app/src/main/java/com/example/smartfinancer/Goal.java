package com.example.smartfinancer;

public class Goal {
    private String id;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private String deadline;

    // Required empty constructor for Firestore
    public Goal() {
    }

    public Goal(String goalName, double targetAmount, double currentAmount, String deadline) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getProgressPercentage() {
        return (int) ((currentAmount / targetAmount) * 100);
    }

    public boolean isCompleted() {
        return currentAmount >= targetAmount;
    }
}

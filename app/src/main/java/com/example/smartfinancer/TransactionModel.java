package com.example.smartfinancer;

public class TransactionModel {
    private float amount;
    private String date;

    public TransactionModel(float amount, String date) {
        this.amount = amount;
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}

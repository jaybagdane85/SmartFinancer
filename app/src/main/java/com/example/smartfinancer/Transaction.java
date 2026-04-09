package com.example.smartfinancer;

public class Transaction {
    private String message;
    private String date;

    public Transaction(String message, String date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}

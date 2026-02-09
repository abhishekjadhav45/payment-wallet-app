package com.abhi.wallet.dto;

public class WalletResponse {
    private double balance;
    private String currency;
    private String message;

    public WalletResponse(double balance, String currency, String message) {
        this.balance = balance;
        this.currency = currency;
        this.message = message;
    }

    // Getters & Setters
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

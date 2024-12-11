package models;

public class Wallet {
    private static final double MAX_BALANCE = 100000000.0;
    private double balance;

    public boolean addFunds(double amount) {
        if (balance + amount <= MAX_BALANCE) {
            balance += amount;
            return true;
        }
        return false;
    }

    public boolean deductFunds(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance;
    }
}
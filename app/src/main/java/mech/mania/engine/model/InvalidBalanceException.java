package mech.mania.engine.model;

public class InvalidBalanceException extends Exception {
    public InvalidBalanceException(double currentBalance, double itemPrice) {
        super(String.format("Insufficient funds to purchase seed: player has %.2f but needs %.2f",
                currentBalance, itemPrice));
    }
}

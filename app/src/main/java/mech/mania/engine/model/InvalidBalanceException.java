package mech.mania.engine.model;

public class InvalidBalanceException extends Exception {
    public InvalidBalanceException(double currentBalance, double itemPrice) {
        super(String.format("Insufficient funds to purchase seed: player has %f but needs %f",
                currentBalance, itemPrice));
    }
}

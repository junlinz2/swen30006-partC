package mycontroller.exceptions;

public class StrategyNotFoundException extends Exception {
    public StrategyNotFoundException() {
        super("specified strategy was not found.");
    }
}

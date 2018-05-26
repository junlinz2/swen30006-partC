package mycontroller.strategies;

import mycontroller.MyAIController;

/**
 * Group 39
 * Interface for basic strategies the CarController could be using.
 */
public interface CarControllerStrategy {

    enum carControllerActions { ACCELERATE, SLOWDOWN, ISTURNINGLEFT, ISTURNINGRIGHT, REVERSE }

    void decideAction(MyAIController carController);
}

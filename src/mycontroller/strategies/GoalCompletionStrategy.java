package mycontroller.strategies;

import mycontroller.AStarSearch;
import mycontroller.MyAIController;

public abstract class GoalCompletionStrategy implements CarControllerStrategy {

    public abstract void decideAction(MyAIController carController);

    public AStarSearch performSearch() {

        return null;
    }
}

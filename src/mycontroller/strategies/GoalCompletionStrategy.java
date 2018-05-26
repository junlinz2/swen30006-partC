package mycontroller.strategies;

import mycontroller.AStarSearch;
import mycontroller.MyAIController;

public abstract class GoalCompletionStrategy implements CarControllerStrategy {

    public abstract void decideAction(MyAIController carController);

    public abstract void startAStarSearch(MyAIController carController);
    
    public enum Movement {STRAIGHT, LEFT, RIGHT};
}

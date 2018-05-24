package mycontroller.strategies;
import mycontroller.MyAIController;
import mycontroller.exceptions.StrategyNotFoundException;

public class StrategyFactory {

    public StrategyFactory(MyAIController c) throws StrategyNotFoundException {

        // set the default behaviour of controller to follow left wall
        CarNavigationStrategy initialStrategy = createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        c.setCarNavigationStrategy(initialStrategy);
    }

    // TODO : add the other strategies
    public CarNavigationStrategy createCarStrategy(MyAIController c, MyAIController.strategies strategyName) {
        switch (strategyName) {
            case FOLLOWLEFTWALL:
                return new FollowLeftWallStrategy(c);
            case FOLLOWRIGHTWALL:
                return new FollowRightWallStrategy(c);
            case HEALING:
                return new HealingStrategy();
            case GOTHROUGHLAVA:
                return new GoThroughLavaStrategy(this, c);
            default:
                return null;
        }
    }
}

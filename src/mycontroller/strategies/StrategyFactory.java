package mycontroller.strategies;
import mycontroller.MyAIController;
import mycontroller.exceptions.StrategyNotFoundException;

public class StrategyFactory {

    public StrategyFactory(MyAIController c) throws StrategyNotFoundException {

        // set the default behaviour of controller to follow left wall
        CarNavigationStrategy initialStrategy = changeCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        c.setCarNavigationStrategy(initialStrategy);
    }

    // TODO : add the other strategies
    public CarNavigationStrategy changeCarStrategy(MyAIController c, MyAIController.strategies strategyName)
            throws StrategyNotFoundException {
        switch (strategyName) {
            case FOLLOWLEFTWALL:
                return new FollowLeftWallStrategy(c);

            default:
                throw new StrategyNotFoundException();
        }
    }
}

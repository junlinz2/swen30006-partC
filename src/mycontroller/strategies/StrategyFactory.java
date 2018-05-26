package mycontroller.strategies;
import mycontroller.MyAIController;

public class StrategyFactory {

    public StrategyFactory(MyAIController c) {
        // set the default behaviour of controller to follow left wall
        CarNavigationStrategy initialStrategy = createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        c.setCarNavigationStrategy(initialStrategy);
    }

    private float strategyCheckTimer = 0;
    private float strategyCheckThreshold = 0.5f;

    public void decideStrategy(MyAIController controller, float delta) {
        if (controller.getLatestGameMap().getNearestHealthTile() != null && controller.getHealth() < controller.HEALING_THRESHOLD) {
            controller.setCarNavigationStrategy(createCarStrategy(controller, MyAIController.strategies.HEALING));
        }
    }

    // TODO : add the other strategies
    public CarNavigationStrategy createCarStrategy(MyAIController c, MyAIController.strategies strategyName) {
        switch (strategyName) {
            case FOLLOWLEFTWALL:
                return new FollowLeftWallStrategy(c);
            case FOLLOWRIGHTWALL:
                return new FollowRightWallStrategy(c);
            case HEALING:
                return new FindHealthTrapStrategy(this, c);
            case GOTHROUGHLAVA:
                return new GoThroughLavaStrategy(this, c);
            case FINDKEY:
                return new FindKeyStrategy();
            default:
                return null;
        }
    }
}

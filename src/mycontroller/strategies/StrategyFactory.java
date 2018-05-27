package mycontroller.strategies;
import mycontroller.MyAIController;

public class StrategyFactory {

    public StrategyFactory(MyAIController c) {
        // set the default behaviour of controller to follow left wall
        CarControllerStrategy initialStrategy = createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        c.setCarNavigationStrategy(initialStrategy);
    }

    // TODO : fill this in
    public void decideStrategy(MyAIController controller) {
        if (controller.getLatestGameMap().getNearestHealthTile() != null && controller.getHealth() < controller.HEALING_THRESHOLD) {
            controller.setCarNavigationStrategy(createCarStrategy(controller, MyAIController.strategies.HEALING));
        }
    }

    // TODO : add the other strategies
    public CarControllerStrategy createCarStrategy(MyAIController c, MyAIController.strategies strategyName) {
        switch (strategyName) {
            case FOLLOWLEFTWALL:
                return new FollowLeftObstacleStrategy(c);
            case FOLLOWRIGHTWALL:
                return new FollowRightObstacleStrategy(c);
            case HEALING:
                return new FindHealthTrapStrategy(c);
            case GOTHROUGHLAVA:
                return new GoThroughLavaStrategy(this, c);
            case FINDKEY:
                return new FindKeyStrategy(c);
            default:
                return null;
        }
    }
}

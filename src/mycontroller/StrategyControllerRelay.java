package mycontroller;

import controller.CarController;
import mycontroller.strategies.CarNavigationStrategy;
import world.WorldSpatial;

/**
 * Group 39
 *
 * Example of pure fabrication, a form of indirection. We used a relay class that calls the appropriate methods in
 * MyAIController according to how the Strategy decides the Controller should act.
 */
public class StrategyControllerRelay {

    private MyAIController carController;

    public StrategyControllerRelay(CarController c) {
        this.carController = (MyAIController) c;
    }

    public void changeState(CarNavigationStrategy.carControllerActions action, float delta) {
        WorldSpatial.Direction orientation = carController.getOrientation();
        switch(action) {
            case TURNRIGHT:
                // carController.applyReverseAcceleration();
                if (carController.getSpeed() <= carController.getMinCarSpeed()) {
                    carController.applyForwardAcceleration();
                    break;
                }
                else if (carController.getSpeed() > carController.getMaxTurningSpeed()) {

                    carController.applyBrake();
                    break;
                }
                carController.applyRightTurn(orientation, delta);
                break;
            case TURNLEFT:
                // carController.applyReverseAcceleration();
                if (carController.getSpeed() <= carController.getMinCarSpeed()) {
                    carController.applyForwardAcceleration();
                    break;
                }
                else if (carController.getSpeed() > carController.getMaxTurningSpeed()) {
                    carController.applyBrake();
                    break;
                }
                carController.applyLeftTurn(orientation, delta);
                break;
            case ACCELERATE:
                // carController.setTurningLeft(false);
                // carController.setTurningRight(false);
                carController.applyForwardAcceleration();
                break;
            case DECELERATE:
                carController.applyBrake();
                break;
            case REVERSE:
                carController.applyReverseAcceleration();
                break;
            case ISTURNINGLEFT:
                carController.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
                carController.setTurningLeft(true);
                carController.applyBrake();
                break;
            case ISTURNINGRIGHT:
                carController.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
                carController.setTurningRight(true);
                carController.applyBrake();
                break;
            case STOPTURNINGLEFT:
                carController.setTurningLeft(false);
                break;
            case STOPTURNINGRIGHT:
                carController.setTurningRight(false);
                break;
            default:
        }
    }
}

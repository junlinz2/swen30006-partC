package mycontroller;

import controller.CarController;
import mycontroller.strategies.CarNavigationStrategy;
import world.WorldSpatial;

/**
 * Group 39
 *
 * Example of pure fabrication, a form of indirection. We used a relay class
 * that calls the appropriate methods in MyAIController according to how the
 * Strategy decides the Controller should act.
 */
public class StrategyControllerRelay {

	private static StrategyControllerRelay instance;

	public static StrategyControllerRelay getInstance() {
	    if (instance == null) {
	        instance = new StrategyControllerRelay();
        }
        return instance;
    }

	public void changeState(MyAIController carController, CarNavigationStrategy.carControllerActions action, float delta) {
		WorldSpatial.Direction orientation = carController.getOrientation();
		switch (action) {
		case TURNRIGHT:
            carController.applyRightTurn(orientation, delta);
			break;
		case TURNLEFT:
			carController.applyLeftTurn(orientation, delta);
			break;
		case ACCELERATE:
			if (carController.getSpeed() < carController.getMaxCarSpeed()) {
				carController.applyForwardAcceleration();				
			}
			break;
		case DECELERATE:
			if (carController.getSpeed() > carController.getMaxTurningSpeed()) {
				carController.applyReverseAcceleration();
			}
			if (carController.getSpeed() < carController.getMinCarSpeed()) {
				carController.applyForwardAcceleration();
			}
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
		    break;
		}
	}
}

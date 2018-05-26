package mycontroller;

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

	public void changeState(MyAIController carController, CarNavigationStrategy.CarControllerActions action) {
		switch (action) {
			case ACCELERATE:
				if (carController.getSpeed() < carController.MAX_CAR_SPEED) {
					carController.applyForwardAcceleration();
				}
				break;
			case SLOWDOWN:
				carController.applyForwardAcceleration();
				if (carController.getSpeed() > carController.MAX_TURNING_SPEED) {
					carController.applyReverseAcceleration();
				}
				break;
			case REVERSE:
				carController.applyReverseAcceleration();
				break;
			case ISTURNINGLEFT:
				carController.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
				carController.setTurningLeft(true);
				break;
			case ISTURNINGRIGHT:
				carController.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
				carController.setTurningRight(true);
				break;
			default:
				break;
		}
	}
}
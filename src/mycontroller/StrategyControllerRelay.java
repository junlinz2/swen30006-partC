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
	
	//Temporary
	//right
	private final float MIN_ROTATING_SPEED = 0.5f;
	//left
	private final float MIN_CORNER_SPEED = 1.15f;
	
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
            if (carController.getSpeed() > carController.MAX_TURNING_SPEED) {
				carController.applyReverseAcceleration();	
			}
            //else if (carController.getSpeed() < carController.getMinCarSpeed()) { 
            else if (carController.getSpeed() < MIN_ROTATING_SPEED) { 
            	carController.applyForwardAcceleration();	
			}
			break;
		case TURNLEFT:
			carController.applyLeftTurn(orientation, delta);
			//if (carController.getSpeed() < carController.getMinCarSpeed()) { 
			if (carController.getSpeed() < MIN_CORNER_SPEED) { 
				carController.applyForwardAcceleration();	
			}
			else if (carController.getSpeed() > carController.MAX_TURNING_SPEED) {
				carController.applyReverseAcceleration();	
			}
			break;
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
//			else if (carController.getSpeed() < carController.getMinCarSpeed()) {
//				carController.applyForwardAcceleration();
//			}
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

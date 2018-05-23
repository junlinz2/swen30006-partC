package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

	public FollowLeftWallStrategy(MyAIController c) {
		sensor = new Sensor(c.getObstacleFollowingSensitivity(), c.getDistToTurn(), c.getDistToSlowDown());
		this.tilesToAvoid = c.getTilesToAvoid();
	}

	public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

		CarNavigationStrategy.carControllerActions nextState;

		if (carController.getIsTurningRight()) {
			// if (checkTileAccuracy(carController.getOrientation(),
			// carController.getCurrentPosition(),
			// carController.getFloatX(), carController.getFloatY())) {
			// nextState = carControllerActions.TURNRIGHT;
			// } else {
			// nextState = carControllerActions.DONOTHING;
			// }
			nextState = carControllerActions.TURNRIGHT;
		}

		// TODO: tweaking strategy for FollowRightWallStrategy.
		else if (carController.getIsTurningLeft()) {

			// Apply the left turn if you are not currently near a wall.
			if (!checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
					carController.getTilesToAvoid())) {
				// if (checkTileAccuracy(carController.getOrientation(),
				// carController.getCurrentPosition(),
				// carController.getFloatX(), carController.getFloatY())) {
				// nextState = carControllerActions.TURNLEFT;
				// } else {
				// nextState = carControllerActions.DONOTHING;
				// }
				nextState = carControllerActions.TURNLEFT;
			} else {
				nextState = carControllerActions.STOPTURNINGLEFT;
			}
		}

		// Try to determine whether or not the car is next to a wall.
		else if (checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
				carController.getTilesToAvoid())) {

			// If there is wall ahead, turn right!
			int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			if (distToObstacle <= sensor.getDistToTurn()) {
				nextState = carControllerActions.ISTURNINGRIGHT;

			} else if (distToObstacle <= sensor.getDistToSlowDown()
					|| peekCorner(carController.getOrientation(), currentView, carController.getCurrentPosition())) {
				nextState = carControllerActions.DECELERATE;
			} else {
				nextState = carControllerActions.ACCELERATE;
			}
		}
		// This indicates that I can do a left turn if I am not turning right
		else {
			nextState = carControllerActions.ISTURNINGLEFT;
		}

		// TODO: remove debug statement
		//System.out.println(carController.getCurrentPosition().x + " " + carController.getFloatX() + " " + nextState);
		System.out.println(nextState);
		
		StrategyControllerRelay.getInstance().changeState(carController, nextState, delta);
	}

	public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
		return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
				currentPosition, tilesToAvoid);
	}

	@Override
	public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
            Coordinate currentPosition) {
		return sensor.peekCorner(orientation, currentView, currentPosition, WorldSpatial.RelativeDirection.LEFT);
	}

	public boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float x, float y) {
		switch (orientation) {
		case WEST:
			return (x - coordinate.x) < 0.4;
		case EAST:
			return (x - coordinate.x) > -0.4;
		case NORTH:
			return (y - coordinate.y) > -0.4;
		case SOUTH:
			return (y - coordinate.y) < 0.4;
		}
		return false;
	}
}

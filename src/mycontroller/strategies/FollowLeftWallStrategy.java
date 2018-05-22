package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

	public FollowLeftWallStrategy(MyAIController c, ArrayList<MapTile> tilesToAvoid) {
		sensor = new Sensor(c.getObstacleFollowingSensitivity(), c.getObstacleTurningSensitivity(), c.getViewSquare());
		this.tilesToAvoid = tilesToAvoid;
	}

	public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

		CarNavigationStrategy.carControllerActions nextState;

		if (carController.getIsTurningRight()) {
			nextState = carControllerActions.TURNRIGHT;
		}

		// TODO: tweaking strategy for FollowRightWallStrategy.
		else if (carController.getIsTurningLeft()) {

			// Apply the left turn if you are not currently near a wall.
			if (!checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
					carController.getTilesToAvoid())) {
				nextState = carControllerActions.TURNLEFT;
			} else {

				nextState = carControllerActions.STOPTURNINGLEFT;
			}
		}

		// Try to determine whether or not the car is next to a wall.
		else if (checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
				carController.getTilesToAvoid())) {

			// If there is wall ahead, turn right!
			int obstacleDistance = checkViewForTile(carController.getOrientation(), currentView,
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			if (obstacleDistance <= sensor.getObstacleTurningSensitivity()) {
				nextState = carControllerActions.ISTURNINGRIGHT;

			} else if (obstacleDistance <= sensor.getCarSightSensitivity() ||
                       sensor.peekCorner(carController.getOrientation(), currentView, carController.getCurrentPosition(),
                               WorldSpatial.RelativeDirection.LEFT)) {
				nextState = carControllerActions.DECELERATE;
			} else {
				nextState = carControllerActions.ACCELERATE;
			}
//			// Maintain some velocity
//			if (carController.getSpeed() < carController.getMaxCarSpeed()) {
//				nextState = carControllerActions.ACCELERATE;
//			}
		}
		// This indicates that I can do a left turn if I am not turning right
		else {
			nextState = carControllerActions.ISTURNINGLEFT;
		}

		// TODO: remove debug statement
		System.out.println(nextState);

		StrategyControllerRelay.getInstance().changeState(carController, nextState, delta);
	}

	public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
		return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
				currentPosition, tilesToAvoid);
	}
}

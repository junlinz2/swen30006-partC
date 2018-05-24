package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

	public FollowLeftWallStrategy(MyAIController c) {
		sensor = new Sensor(c.OBSTACLE_FOLLOWING_SENSITIVITY, c.DISTANCE_TO_TURN, c.DISTANCE_TO_SLOW_DOWN);
		this.tilesToAvoid = c.getTilesToAvoid();
	}

	public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {
		CarNavigationStrategy.carControllerActions nextState;

		if (carController.getIsTurningRight()) {
			nextState = carControllerActions.TURNRIGHT;
		}

		else if (carController.getIsTurningLeft()) {
			nextState = carControllerActions.TURNLEFT;
		}

		// Try to determine whether or not the car is next to a wall.
		else if (checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
				carController.getTilesToAvoid())) {
			if (carController.isJustChangedState()) {
				carController.setJustChangedState(false);
			}

			int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			// If there is wall ahead, turn right!
			if (distToObstacle <= sensor.getDistToTurn()) {
				nextState = carControllerActions.ISTURNINGRIGHT;
			}

			// Slow down the car when it's going to turn soon
			else if (distToObstacle <= sensor.getDistToSlowDown() || peekCorner(carController.getOrientation(),
					currentView, carController.getCurrentPosition(), carController.getTilesToAvoid())) {
				nextState = carControllerActions.SLOWDOWN;
			}

			else {
				nextState = carControllerActions.ACCELERATE;
			}
		}

		// Ensure the car can find a new wall/lava to follow after turning left by
		// keeping it drive along the new orientation
		else if (carController.justChangedState()
				&& carController.getLastTurnDirection() == WorldSpatial.RelativeDirection.LEFT) {
			nextState = carControllerActions.SLOWDOWN;
		}

		// This indicates that I can do a left turn if the car is no longer cruising
		// along an obstacle
		else {
			// Turn left if the car is not turning into a deadend
			if (!isDeadEnd(carController.getOrientation(), currentView, carController.getCurrentPosition(),
					carController.getTilesToAvoid())) {
				nextState = carControllerActions.ISTURNINGLEFT;
			}

			// If it's a deadend, keep driving in the current orientation until the next
			// turn
			else {
				int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
						carController.getCurrentPosition(), carController.getTilesToAvoid());

				if (distToObstacle <= sensor.getDistToTurn()) {
					nextState = carControllerActions.ISTURNINGRIGHT;
				}

				else if (distToObstacle <= sensor.getDistToSlowDown()) {
					nextState = carControllerActions.SLOWDOWN;
				}

				else {
					nextState = carControllerActions.ACCELERATE;
				}
			}
		}

		// TODO: remove debug statement
		System.out.println(nextState);

		// New action is relayed by the StrategyControllerRelay singleton to
		// MyAIController
		StrategyControllerRelay.getInstance().changeState(carController, nextState, delta);
	}

	public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
		return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
				currentPosition, tilesToAvoid);
	}

	@Override
	public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		return sensor.peekCorner(orientation, currentView, currentPosition, WorldSpatial.RelativeDirection.LEFT,
				tilesToCheck);
	}

	@Override
	public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
		return sensor.isDeadEnd(orientation, currentView, WorldSpatial.RelativeDirection.LEFT, currentPosition,
				tilesToAvoid);
	}

	// TODO: Remove this if not used
	public boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float x, float y) {
		switch (orientation) {
		case WEST:
			return (x - coordinate.x) < 0.475;
		case EAST:
			return (x - coordinate.x) > -0.55;
		case NORTH:
			return (y - coordinate.y) > -0.475;
		case SOUTH:
			return (y - coordinate.y) < 0.7;
		}
		return false;
	}
}

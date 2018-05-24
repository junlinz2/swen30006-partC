package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

	public FollowLeftWallStrategy(MyAIController c) {
		setSensor(new Sensor(c.getObstacleFollowingSensitivity(), c.getDistToTurn(), c.getDistToSlowDown()));
		this.setTilesToAvoid(c.getTilesToAvoid());
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

			if (carController.isJustChangedState()) {
				carController.setJustChangedState(false);
			}

			// If there is wall ahead, turn right!
			int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			if (distToObstacle <= getSensor().getDistToTurn()) {
				nextState = carControllerActions.ISTURNINGRIGHT;

			} else if (distToObstacle <= getSensor().getDistToSlowDown()
					|| peekCorner(carController.getOrientation(), currentView, carController.getCurrentPosition())) {
				nextState = carControllerActions.DECELERATE;
			} else {
				nextState = carControllerActions.ACCELERATE;
			}
		}
		// This indicates that I can do a left turn if I am not turning right
		else {
			if (carController.justChangedState()) {
				nextState = carControllerActions.DECELERATE;
			} else {
				nextState = carControllerActions.ISTURNINGLEFT;
			}
		}

		// TODO: remove debug statement
		//System.out.println(carController.getCurrentPosition().x + " " + carController.getFloatX() + " " + nextState);
		System.out.println(nextState);

		StrategyControllerRelay.getInstance().changeState(carController, nextState, delta);
	}

	public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
										  Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
		return getSensor().checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
				currentPosition, tilesToAvoid);
	}

	public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
							  Coordinate currentPosition) {
		return getSensor().peekCorner(orientation, currentView, currentPosition, WorldSpatial.RelativeDirection.LEFT);
	}

	//TODO: magic values here.
	public boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float xPos, float yPos) {
	    //float accuracyThreshold = 0.4f;
		switch (orientation) {
			case WEST:
				return (xPos - coordinate.x) < (0.475);
			case EAST:
				return (xPos - coordinate.x) > -(0.55);
			case NORTH:
				return (yPos - coordinate.y) > -(0.475);
			case SOUTH:
				return (yPos - coordinate.y) < (0.7);
		}
		return false;
	}
}
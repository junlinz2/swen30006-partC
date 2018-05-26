package mycontroller.strategies;

import mycontroller.GameMap;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import mycontroller.TilesChecker;
import mycontroller.strategies.CarNavigationStrategy.CarControllerActions;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//TODO document this
public abstract class CarNavigationStrategy {

	// Different strategies manipulate the behaviour of the sensor, so we need a
	// reference to it
	protected Sensor sensor;
	protected ArrayList<MapTile> tilesToAvoid;
	protected boolean changeStrategyNow = false;

	public CarNavigationStrategy(ArrayList<MapTile> tilesToAvoid, int tileFollowingSensitivity, int distToSlowDown ) {
		sensor = new Sensor(tileFollowingSensitivity, distToSlowDown);
		this.tilesToAvoid = tilesToAvoid;
	} 
	
	public abstract void decideAction(HashMap<Coordinate, MapTile> currentView, MyAIController carController);

	/**
	 * Adjusts the speed of the car before a turning event occurs and decides when
	 * the car should turn to avoid obstacle
	 * 
	 * @param distToObstacle
	 * @param The
	 *            relativeDirection the car takes when there is an obstacle ahead
	 * @param maxDistToTurn
	 * @param maxDistToSlowDown
	 * @param is
	 *            the followed tiles ending ahead? (Note: the car won't take the
	 *            turningDirection if this is true)
	 * @return
	 */
	public static CarControllerActions decideTurning(int distToObstacle,
			WorldSpatial.RelativeDirection turningDirection, int maxDistToTurn, int maxDistToSlowDown,
			boolean followedTilesEndAhead) {
		// If there is wall ahead, turn right!
		if (distToObstacle <= maxDistToTurn) {
			if (turningDirection == WorldSpatial.RelativeDirection.LEFT) {
				return CarControllerActions.ISTURNINGLEFT;
			} else {
				return CarControllerActions.ISTURNINGRIGHT;
			}
		}

		// Slow down the car when it's going to turn soon
		else if (distToObstacle <= maxDistToSlowDown || followedTilesEndAhead) {
			return CarControllerActions.SLOWDOWN;
		}

		else {
			return CarControllerActions.ACCELERATE;
		}
	}

	/**
	 * Adjusts the speed of the car before a turning event occurs and decides when
	 * the car should turn to avoid obstacle
	 * 
	 * @param distToObstacle
	 * @param The
	 *            relativeDirection the car takes when there is an obstacle ahead
	 * @param maxDistToTurn
	 * @param maxDistToSlowDown
	 * @return
	 */
	public static CarControllerActions decideTurning(int distToObstacle,
			WorldSpatial.RelativeDirection turningDirection, int maxDistToTurn, int maxDistToSlowDown) {
		// If there is wall ahead, turn right!
		if (distToObstacle <= maxDistToTurn) {
			if (turningDirection == WorldSpatial.RelativeDirection.LEFT) {
				return CarControllerActions.ISTURNINGLEFT;
			} else {
				return CarControllerActions.ISTURNINGRIGHT;
			}
		}

		// Slow down the car when it's going to turn soon
		else if (distToObstacle <= maxDistToSlowDown) {
			return CarControllerActions.SLOWDOWN;
		}

		else {
			return CarControllerActions.ACCELERATE;
		}
	}

	public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation,
			HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

	public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		return sensor.checkDistToObstacleAhead(orientation, currentView, currentPosition, tilesToCheck);
	}

	public enum CarControllerActions {
		ACCELERATE, SLOWDOWN, ISTURNINGLEFT, ISTURNINGRIGHT, REVERSE, DONOTHING
	}

	public abstract boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);

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

	public abstract boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

	public abstract LinkedHashMap<Coordinate, MapTile> getOrientationViewInFollowingDirection(
			HashMap<Coordinate, MapTile> currentView, WorldSpatial.Direction orientation, Coordinate currentPosition);

	public Coordinate getFollowingObstacle(HashMap<Coordinate, MapTile> currentView, Direction orientation,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		
		LinkedHashMap<Coordinate, MapTile> viewInFollowingDirection = getOrientationViewInFollowingDirection(
				currentView, orientation, currentPosition);

		int i = 1;
		for (Map.Entry<Coordinate, MapTile> tileInView : viewInFollowingDirection.entrySet()) {
			for (MapTile tile : tilesToCheck) {
				if (TilesChecker.checkTileTypeSame(tile, tileInView.getValue())
						&& i <= sensor.getTileFollowingSensitivity())
					return tileInView.getKey();
			}
			i++;

			if (i > sensor.getTileFollowingSensitivity()) {
				break;
			}
		}

		return null;
	}

	public abstract boolean findTurningPointForNewStrategy(MyAIController carController, ArrayList<Coordinate> obstaclesToFollow,
			WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition,
			ArrayList<MapTile> tilesToCheck);
	
	public boolean changeStrategyNow() {
		return changeStrategyNow;
	}

	public abstract Coordinate findTilesOnOtherSide(HashMap<Coordinate, MapTile> currentView, Direction orientation,
			Coordinate currentPosition);
}

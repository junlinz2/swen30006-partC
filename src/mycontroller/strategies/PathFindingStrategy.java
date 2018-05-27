
package mycontroller.strategies;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import mycontroller.TilesChecker;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//TODO document this
public abstract class PathFindingStrategy implements CarControllerStrategy {

	// Different strategies manipulate the behaviour of the sensor, so we need a
	// reference to it
	protected Sensor sensor;
	protected ArrayList<MapTile> tilesToAvoid;
	protected boolean changeStrategyNow = false;
	public final int DISTANCE_TO_CHECK_FOR_TURNING_POINT = 1;

	
	public abstract void decideAction(MyAIController carController);

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
	 * Adjusts the speed of the car before a turning event occurs and decides
	 * when the car should turn to avoid obstacle
	 * 
	 * @param distToObstacle
	 * @param The
	 *            relativeDirection the car takes when there is an obstacle
	 *            ahead
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


	public abstract boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);

	public abstract boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

	public abstract LinkedHashMap<Coordinate, MapTile> getOrientationViewInFollowingDirection(
			HashMap<Coordinate, MapTile> currentView, WorldSpatial.Direction orientation, Coordinate currentPosition);

	public Coordinate getFollowingObstacle(HashMap<Coordinate, MapTile> currentView, Direction orientation,
			Coordinate currentPosition) {

		LinkedHashMap<Coordinate, MapTile> viewInFollowingDirection = getOrientationViewInFollowingDirection(
				currentView, orientation, currentPosition);

		int i = 1;
		for (Map.Entry<Coordinate, MapTile> tileInView : viewInFollowingDirection.entrySet()) {
			for (MapTile tile : tilesToAvoid) {
				if (TilesChecker.checkTileSameType(tile, tileInView.getValue())
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

	public abstract CarControllerActions findTurningPointForNewStrategy(MyAIController carController,
			ArrayList<Coordinate> obstaclesToFollow, WorldSpatial.Direction orientation,
			HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition);

	public boolean changeStrategyNow() {
		return changeStrategyNow;
	}

	public abstract Coordinate findTileOnOtherSide(HashMap<Coordinate, MapTile> currentView, Direction orientation,
			Coordinate currentPosition);
	
	public ArrayList<MapTile> getTilesToAvoid() {
		return tilesToAvoid;
	}
}

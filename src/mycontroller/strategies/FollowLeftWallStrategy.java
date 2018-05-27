package mycontroller.strategies;

import mycontroller.*;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.*;

public class FollowLeftWallStrategy extends PathFindingStrategy {

	public FollowLeftWallStrategy(int tileFollowingSensitivity, int distToSlowDown) {
		sensor = new Sensor(tileFollowingSensitivity, distToSlowDown);
		super.tilesToAvoid = new ArrayList<>();
		tilesToAvoid.add(new MapTile(MapTile.Type.WALL));
		tilesToAvoid.add(new LavaTrap());
	}

	@Override
	public void decideAction(MyAIController carController) {

		PathFindingStrategy.CarControllerActions nextState;

		// When the car just finishes turning and is searching for an obstacle ahead to
		// switch strategy
		if (carController.getActionAtTurningPoint() != null && carController.justChangedState()) {
			int distToObstacle = checkViewForTile(carController.getOrientation(), carController.getView(),
					carController.getCurrentPosition(), tilesToAvoid);
			// Turn left when an obstacle is ahead so that the obstacle will be on the right
			// in order to use followRightWallStrategy.
			nextState = decideTurning(distToObstacle, WorldSpatial.RelativeDirection.LEFT,
					carController.DISTANCE_TO_TURN, carController.DISTANCE_TO_SLOW_DOWN);

			if (nextState == CarControllerActions.ISTURNINGLEFT) {
				carController.setActionAtTurningPoint(null);
				carController.setJustChangedState(false);
				changeStrategyNow = true;
			}
		}

		// Try to determine whether or not the car is next to a wall.
		else if (checkFollowingObstacle(carController.getOrientation(), carController.getView(), carController.getCurrentPosition(),
				tilesToAvoid)) {

			if (carController.justChangedState()) {
				carController.setJustChangedState(false);
			}

			int distToObstacle = checkViewForTile(carController.getOrientation(), carController.getView(),
					carController.getCurrentPosition(), tilesToAvoid);
			boolean followedTilesEndAhead = peekCorner(carController.getOrientation(), carController.getView(),
					carController.getCurrentPosition(), tilesToAvoid);
			// If there is wall ahead, turn right!
			// Or slow down the car when it's going to turn soon
			nextState = decideTurning(distToObstacle, WorldSpatial.RelativeDirection.RIGHT,
					carController.DISTANCE_TO_TURN, carController.DISTANCE_TO_SLOW_DOWN, followedTilesEndAhead);
		}

		// Ensure the car can find a new wall/lava to follow after turning left when
		// previous followed obstacles ended by keeping it drive along the new
		// orientation
		else if (carController.justChangedState()
				&& carController.getLastTurnDirection() == WorldSpatial.RelativeDirection.LEFT) {
			nextState = CarControllerActions.SLOWDOWN;
		}

		// This indicates that I can do a left turn if the car is no longer cruising
		// along an obstacle
		else {
			// Turn left if the car is not turning into a deadend
			if (!isDeadEnd(carController.getOrientation(), carController.getView(), carController.getCurrentPosition(),
					tilesToAvoid)) {
				nextState = CarControllerActions.ISTURNINGLEFT;
			}

			// If it's a deadend, keep driving in the current orientation until the next
			// turn
			else {
				int distToObstacle = checkViewForTile(carController.getOrientation(), carController.getView(),
						carController.getCurrentPosition(), tilesToAvoid);
				nextState = decideTurning(distToObstacle, WorldSpatial.RelativeDirection.RIGHT,
						carController.DISTANCE_TO_TURN, carController.DISTANCE_TO_SLOW_DOWN);
			}
		}

		// TODO: remove debug statement
		System.out.println(nextState);

		// New action is relayed by the StrategyControllerRelay singleton to
		// MyAIController
		StrategyControllerRelay.getInstance().changeState(carController, nextState);
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

	@Override
	public LinkedHashMap<Coordinate, MapTile> getOrientationViewInFollowingDirection(
			HashMap<Coordinate, MapTile> currentView, Direction orientation, Coordinate currentPosition) {
		return sensor.getOrientationViewInFollowingDirection(currentView, orientation,
				WorldSpatial.RelativeDirection.LEFT, currentPosition);
	}

	@Override
	public CarControllerActions findTurningPointForNewStrategy(MyAIController carController,
			ArrayList<Coordinate> obstaclesToFollow, WorldSpatial.Direction orientation,
			HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		Coordinate obstacleOnRight = findTileOnOtherSide(currentView, orientation, currentPosition);
		if (obstaclesToFollow.contains(obstacleOnRight)) {
			StrategyControllerRelay.getInstance().changeState(carController, CarControllerActions.ISTURNINGRIGHT);
			return CarControllerActions.ISTURNINGRIGHT;
		}

		// Check ahead to slow down before turning
		LinkedHashMap<Coordinate, MapTile> viewAhead = sensor.getOrientationView(orientation, currentView,
				currentPosition);
		int i = 0;
		for (Map.Entry<Coordinate, MapTile> tileInView : viewAhead.entrySet()) {
			i++;
			if (i > DISTANCE_TO_CHECK_FOR_TURNING_POINT) {
				return null;
			}
			
			obstacleOnRight = findTileOnOtherSide(currentView, orientation, tileInView.getKey());
			if (TilesChecker.checkTileTraversable(tileInView.getValue(), tilesToAvoid)
					&& obstaclesToFollow.contains(obstacleOnRight)) {
				StrategyControllerRelay.getInstance().changeState(carController, CarControllerActions.SLOWDOWN);
				return CarControllerActions.SLOWDOWN;
			} 
		}

		return null;
	}

	public Coordinate findTileOnOtherSide(HashMap<Coordinate, MapTile> currentView, WorldSpatial.Direction orientation,
			Coordinate currentPosition) {
		switch (orientation) {
		case NORTH:
			return sensor.findClosestObstacleInOrientation(WorldSpatial.Direction.EAST, currentView, currentPosition,
					tilesToAvoid);
		case SOUTH:
			return sensor.findClosestObstacleInOrientation(WorldSpatial.Direction.WEST, currentView, currentPosition,
					tilesToAvoid);
		case EAST:
			return sensor.findClosestObstacleInOrientation(WorldSpatial.Direction.SOUTH, currentView, currentPosition,
					tilesToAvoid);
		case WEST:
			return sensor.findClosestObstacleInOrientation(WorldSpatial.Direction.NORTH, currentView, currentPosition,
					tilesToAvoid);
		default:
			return null;
		}
	}


}
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

	public void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {
		CarNavigationStrategy.carControllerActions nextState;

		// Try to determine whether or not the car is next to a wall.
		if (checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
				carController.getTilesToAvoid())) {
			if (carController.justChangedState()) {
				carController.setJustChangedState(false);
			}

			deregisterFollowedObstacles(carController.getLatestGameMap(), currentView, carController.getOrientation(),
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
					carController.getCurrentPosition(), carController.getTilesToAvoid());

			// If there is wall ahead, turn right!
			if (distToObstacle <= carController.DISTANCE_TO_TURN) {
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

				if (distToObstacle <= carController.DISTANCE_TO_TURN) {
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

	public void registerObstaclesToFollow(GameMap gameMap, HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		int distToObstacle = Integer.MAX_VALUE;
		Coordinate obstacleCoordinate = null;

		switch (orientation) {
		case NORTH:
			distToObstacle = sensor.checkViewForTile(WorldSpatial.Direction.EAST, currentView, currentPosition,
					tilesToCheck);
			obstacleCoordinate = new Coordinate(currentPosition.x + distToObstacle, currentPosition.y);
		case SOUTH:
			distToObstacle = sensor.checkViewForTile(WorldSpatial.Direction.WEST, currentView, currentPosition,
					tilesToCheck);
			obstacleCoordinate = new Coordinate(currentPosition.x - distToObstacle, currentPosition.y);
		case EAST:
			distToObstacle = sensor.checkViewForTile(WorldSpatial.Direction.SOUTH, currentView, currentPosition,
					tilesToCheck);
			obstacleCoordinate = new Coordinate(currentPosition.x, currentPosition.y - distToObstacle);
		case WEST:
			distToObstacle = sensor.checkViewForTile(WorldSpatial.Direction.NORTH, currentView, currentPosition,
					tilesToCheck);
			obstacleCoordinate = new Coordinate(currentPosition.x, currentPosition.y + distToObstacle);
		}

		if (distToObstacle <= sensor.getDistToSlowDown()) {
			if (!gameMap.getObstaclesToFollow().contains(obstacleCoordinate)
					&& !gameMap.getUpdatedMap().get(obstacleCoordinate).isFollowed()) {
				gameMap.getObstaclesToFollow().add(obstacleCoordinate);
			}
		}
	}

	public void deregisterFollowedObstacles(GameMap gameMap, HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		LinkedHashMap<Coordinate, MapTile> view = sensor.getOrientationView(currentView, orientation,
				WorldSpatial.RelativeDirection.LEFT, currentPosition);

		for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
			for (MapTile tile : tilesToCheck) {
				if (currentPosition.x == 1 && currentPosition.y == 18) {
					System.out.println("testing]=======================================================");
				}
				if (TilesChecker.checkTileTypeSame(tileInView.getValue(), tile)) {
					gameMap.getObstaclesToFollow().remove(tileInView.getKey());
					gameMap.getUpdatedMap().get(tileInView.getKey()).setFollowed(true);
					return;
				}
			}
		}
	}
}
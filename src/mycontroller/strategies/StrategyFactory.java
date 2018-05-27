package mycontroller.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import mycontroller.MyAIController;
import mycontroller.TilesChecker;
import mycontroller.exceptions.StrategyNotFoundException;
import mycontroller.strategies.CarNavigationStrategy.CarControllerActions;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class StrategyFactory {

	// StrategyFactory should decide when to change strategies, so the list
	// obstaclesToFollow should be here
	// Followed obstacles should be here in a list as well instead of in GameMap
	private ArrayList<Coordinate> obstaclesToFollow = new ArrayList<>();
	private ArrayList<Coordinate> followedObstacles = new ArrayList<>();
	private MyAIController.Strategies currentStrategyName = null;
	private CarNavigationStrategy currentStrategy = null;
	private Coordinate switchingPoint = null;
	private boolean justFoundSwitchingPoint = false;
	private boolean searchForTurningPoint = false;
	private CarControllerActions actionAtTurningPoint = null;

	// TODO : add the other strategies
	public CarNavigationStrategy createCarStrategy(int tileFollowingSensitivity,
			int distToSlowDown, MyAIController.Strategies strategyName){

		CarNavigationStrategy newStrategy = null;
		switch (strategyName) {
		case FOLLOWLEFTWALL:
			currentStrategyName = MyAIController.Strategies.FOLLOWLEFTWALL;
			newStrategy = new FollowLeftWallStrategy(tileFollowingSensitivity, distToSlowDown);
			break;
		case FOLLOWRIGHTWALL:
			currentStrategyName = MyAIController.Strategies.FOLLOWRIGHTWALL;
			newStrategy = new FollowRightWallStrategy(tileFollowingSensitivity, distToSlowDown);
			break;
		default:
			break;
		}

		this.currentStrategy = newStrategy;
		return newStrategy;
	}

	public CarNavigationStrategy changeCarStrategy(ArrayList<MapTile> tilesToAvoid, int tileFollowingSensitivity,
			int distToSlowDown) {
		// Set it null to get a new switching point in myAiController
		switchingPoint = null;

		if (currentStrategyName == MyAIController.Strategies.FOLLOWLEFTWALL) {
			currentStrategyName = MyAIController.Strategies.FOLLOWRIGHTWALL;
			currentStrategy = new FollowRightWallStrategy(tilesToAvoid, tileFollowingSensitivity, distToSlowDown);
			return currentStrategy;
		}

		else if (currentStrategyName == MyAIController.Strategies.FOLLOWRIGHTWALL) {
			currentStrategyName = MyAIController.Strategies.FOLLOWLEFTWALL;
			currentStrategy = new FollowLeftWallStrategy(tilesToAvoid, tileFollowingSensitivity, distToSlowDown);
			return currentStrategy;
		}

		return null;
	}

	public void registerTilesToFollow(HashMap<Coordinate, MapTile> currentView, WorldSpatial.Direction orientation,
			Coordinate currentPosition) {

		Coordinate tileCoordinate = currentStrategy.findTileOnOtherSide(currentView, orientation, currentPosition);

		if (tileCoordinate != null) {
			if (!obstaclesToFollow.contains(tileCoordinate) && !followedObstacles.contains(tileCoordinate)) {
				obstaclesToFollow.add(tileCoordinate);
			}
		}
	}

	public void deregisterFollowedObstacles(HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {

		LinkedHashMap<Coordinate, MapTile> viewInFollowingDirection = currentStrategy
				.getOrientationViewInFollowingDirection(currentView, orientation, currentPosition);

		for (Map.Entry<Coordinate, MapTile> tileInView : viewInFollowingDirection.entrySet()) {
			for (MapTile tile : tilesToCheck) {
				if (TilesChecker.checkTileTypeSame(tileInView.getValue(), tile)) {
					obstaclesToFollow.remove(tileInView.getKey());

					// TODO: Use HashSet if possible
					if (!followedObstacles.contains(tileInView.getKey())) {
						followedObstacles.add(tileInView.getKey());
					}

					return;
				}
			}
		}
	}

	public CarControllerActions monitorStrategyChange(MyAIController carController,
			HashMap<Coordinate, MapTile> currentView, WorldSpatial.Direction orientation, Coordinate currentPosition) {
		Coordinate currentFollowingObstacle = currentStrategy.getFollowingObstacle(currentView, orientation,
				currentPosition);

		if (currentFollowingObstacle != null && switchingPoint == null) {
			switchingPoint = currentFollowingObstacle;
			justFoundSwitchingPoint = true;
		}

		if (justFoundSwitchingPoint && currentFollowingObstacle != null && switchingPoint != currentFollowingObstacle) {
			justFoundSwitchingPoint = false;
		}

		if (!justFoundSwitchingPoint && actionAtTurningPoint == null && currentFollowingObstacle != null
				&& currentFollowingObstacle.equals(switchingPoint)) {
			searchForTurningPoint = true;
		}

		if (searchForTurningPoint) {
			actionAtTurningPoint = currentStrategy.findTurningPointForNewStrategy(carController, getObstaclesToFollow(),
					orientation, currentView, currentPosition);
			if (actionAtTurningPoint != null) {
				if (actionAtTurningPoint == CarControllerActions.ISTURNINGRIGHT
						|| actionAtTurningPoint == CarControllerActions.ISTURNINGLEFT) {
					searchForTurningPoint = false;
				}
				return actionAtTurningPoint;
			}
		}

		return null;
	}

	public Coordinate getSwitchingPoint() {
		return switchingPoint;
	}

	public void setSwitchingPoint(Coordinate switchingPoint) {
		this.switchingPoint = switchingPoint;
	}

	public ArrayList<Coordinate> getObstaclesToFollow() {
		return obstaclesToFollow;
	}

	public void setObstaclesToFollow(ArrayList<Coordinate> obstaclesToFollow) {
		this.obstaclesToFollow = obstaclesToFollow;
	}

	public MyAIController.Strategies getCurrentStrategyName() {
		return currentStrategyName;
	}
}

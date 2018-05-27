package mycontroller.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import mycontroller.MyAIController;
import mycontroller.TilesChecker;
import mycontroller.exceptions.StrategyNotFoundException;
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

	// TODO : add the other strategies
	// TODO: remove this if unused
	public CarNavigationStrategy createCarStrategy(int tileFollowingSensitivity,
			int distToSlowDown, MyAIController.Strategies strategyName) throws StrategyNotFoundException {

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
		//Set it null to get a new switching point in myAiController
		switchingPoint = null;
		
		if (currentStrategyName == MyAIController.Strategies.FOLLOWLEFTWALL) {
			currentStrategyName = MyAIController.Strategies.FOLLOWRIGHTWALL;
			currentStrategy = new FollowRightWallStrategy(tileFollowingSensitivity, distToSlowDown);
			return currentStrategy;
		}

		else if (currentStrategyName == MyAIController.Strategies.FOLLOWRIGHTWALL) {
			currentStrategyName = MyAIController.Strategies.FOLLOWLEFTWALL;
			currentStrategy = new FollowLeftWallStrategy(tileFollowingSensitivity, distToSlowDown);
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
		
//		if (currentPosition.equals(new Coordinate(3, 13))) {
//			for (Coordinate coord : obstaclesToFollow) {
//				System.out.println(coord.x + ",  " + (34 -coord.y));
//				
//			}
//		}
		if (currentPosition.equals(new Coordinate(19, 18)) || currentPosition.equals(new Coordinate(14, 31))) {
			for (Coordinate coord : obstaclesToFollow) {
				System.out.println(coord.x + ",  " + (34 -coord.y));
				
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
}

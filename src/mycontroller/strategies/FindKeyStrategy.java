package mycontroller.strategies;

import java.util.HashMap;
import java.util.List;
import mycontroller.AStarSearch;
import mycontroller.GameMap;
import mycontroller.HashMapTile;
import mycontroller.MyAIController;
import mycontroller.Node;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

public class FindKeyStrategy extends GoalCompletionStrategy {

	private Node carCurrentNode;
	private Node keyNode;
	private List<Node> path;
	private List<GoalCompletionStrategy.Movement> movement;
	private WorldSpatial.Direction currentOrientation;

	public FindKeyStrategy(MyAIController c) {
		// TODO Auto-generated constructor stub
		startAStarSearch(c);
		currentOrientation = c.getOrientation();
		routeInterpretor(path, currentOrientation);
	}
	
	@Override
	public void decideAction(MyAIController carController) {
		// TODO Auto-generated method stub
		
		
	}
	


	public Node findNextKey(MyAIController carController) {
		GameMap gameMap = carController.getLatestGameMap();
		Coordinate keyPosition = gameMap.getNextKeyCoordinate();
		MapTile keyTile = gameMap.getUpdatedMap().get(keyPosition).getTile();
		keyNode = new Node(keyPosition.x, keyPosition.y, keyTile);
		return keyNode;
	}

	public void startAStarSearch(MyAIController carController) {
		HashMap<Coordinate, HashMapTile> updateMap = carController.getLatestGameMap().getUpdatedMap();
		int carX = carController.getCurrentPosition().x;
		int carY = carController.getCurrentPosition().y;
		keyNode = findNextKey(carController);
		carCurrentNode = new Node(carX, carY, updateMap.get(new Coordinate(carX, carY)).getTile());
		AStarSearch aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, carCurrentNode, keyNode, updateMap);
		path = aStar.findPath();
	}
	
	public void routeInterpretor(List<Node> path, WorldSpatial.Direction orientation) {
		while (path.size() > 1) {
			Node startPosition = path.get(0);
			Node nextPosition = path.get(1);
			movement.add(checkNextMove(startPosition, nextPosition));
			path.remove(0);
		}

	}

	public GoalCompletionStrategy.Movement checkNextMove(Node startPosition, Node nextPosition) {
		int xChanges = startPosition.getX() - nextPosition.getX();
		int yChanges = startPosition.getY() - nextPosition.getY();
		switch (currentOrientation) {
		case SOUTH:
			if (xChanges < 0) {
				// turn right
				currentOrientation = WorldSpatial.Direction.WEST;
				return GoalCompletionStrategy.Movement.RIGHT;
			} else if (xChanges > 0) {
				// turnleft
				currentOrientation = WorldSpatial.Direction.EAST;
				return GoalCompletionStrategy.Movement.LEFT;
			} else {
				// straight
				return GoalCompletionStrategy.Movement.STRAIGHT;
			}
		case NORTH:
			if (xChanges < 0) {
				// turn left
				currentOrientation = WorldSpatial.Direction.WEST;
				return GoalCompletionStrategy.Movement.LEFT;
			} else if (xChanges > 0) {
				// turnright
				currentOrientation = WorldSpatial.Direction.EAST;
				return GoalCompletionStrategy.Movement.RIGHT;
			} else {
				// straight
				return GoalCompletionStrategy.Movement.STRAIGHT;
			}
		case EAST:
			if (yChanges < 0) {
				// left
				currentOrientation = WorldSpatial.Direction.SOUTH;
				return GoalCompletionStrategy.Movement.RIGHT;
			} else if (yChanges > 0) {
				// right
				currentOrientation = WorldSpatial.Direction.NORTH;
				return GoalCompletionStrategy.Movement.LEFT;
			} else {
				// straight
				return GoalCompletionStrategy.Movement.STRAIGHT;
			}
		case WEST:
			if (yChanges < 0) {
				// left
				currentOrientation = WorldSpatial.Direction.SOUTH;
				return GoalCompletionStrategy.Movement.LEFT;
			} else if (yChanges > 0) {
				// right
				currentOrientation = WorldSpatial.Direction.NORTH;
				return GoalCompletionStrategy.Movement.RIGHT;
			} else {
				// straight
				return GoalCompletionStrategy.Movement.STRAIGHT;
			}
		}
		return null;
	}
}

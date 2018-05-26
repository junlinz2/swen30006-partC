package mycontroller.strategies;
import mycontroller.GameMap;
import mycontroller.MyAIController;
import mycontroller.Node;
import tiles.MapTile;
import utilities.Coordinate;

public class FindKeyStrategy extends GoalCompletionStrategy {

	private Node keyNode;

	public FindKeyStrategy(MyAIController c) {
		// TODO Auto-generated constructor stub
        keyNode = findNextKey(c);
		startAStarSearch(keyNode, c);
		currentOrientation = c.getOrientation();
		routeInterpretor(path, currentOrientation);
	}
	
	@Override
	public void decideAction(MyAIController carController) {
        // TODO declare targetNode
        keyNode = findNextKey(carController);
    }

    public Node findNextKey(MyAIController carController) {
		GameMap gameMap = carController.getLatestGameMap();
		Coordinate keyPosition = gameMap.getNextKeyCoordinate();
		MapTile keyTile = gameMap.getUpdatedMap().get(keyPosition).getTile();
		keyNode = new Node(keyPosition.x, keyPosition.y, keyTile);
		return keyNode;
	}
}

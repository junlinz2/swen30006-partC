package mycontroller.strategies;

import mycontroller.AStarSearch;
import mycontroller.MyAIController;
import mycontroller.Node;
import utilities.Coordinate;
import world.World;

public class FindKeyStrategy extends GoalCompletionStrategy {

    private Node carCurrentPositionTile;
    private Node keyPosition;
    private AStarSearch aStar;

    public FindKeyStrategy(MyAIController c) {

    }

    public void decideAction(MyAIController carController) {

        carCurrentPositionTile = new Node(carController.getCurrentPosition().x, carController.getCurrentPosition().y,
                carController.getLatestGameMap().getUpdatedMap().get(new Coordinate(carController.getCurrentPosition().x,
                        carController.getCurrentPosition().y)).getTile());

        keyPosition = findNextKey(carController);
        aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, carCurrentPositionTile, keyPosition,
                carController.getLatestGameMap().getUpdatedMap());

    }

    public Node findNextKey(MyAIController c) {
         //TODO: change for game key getting instead
         keyPosition = new Node(c.getLatestGameMap().getNextKeyCoordinate().x, c.getLatestGameMap().getNextKeyCoordinate().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getLatestGameMap().getNextKeyCoordinate().x,
                        c.getLatestGameMap().getNextKeyCoordinate().y)).getTile());
         return keyPosition;
    }
}

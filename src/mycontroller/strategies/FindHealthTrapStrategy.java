package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;

public class FindHealthTrapStrategy extends GoalCompletionStrategy {

    private Node nearestHealthNode;

    public FindHealthTrapStrategy(MyAIController c) {
        nearestHealthNode = findNearestHealthNode(c);
        startAStarSearch(nearestHealthNode, c);
        currentOrientation = c.getOrientation();
        routeInterpretor(path, currentOrientation);
    }

    private Node findNearestHealthNode(MyAIController carController) {
        GameMap gameMap = carController.getLatestGameMap();
        Coordinate healthPosition = gameMap.getNearestHealthTile();
        MapTile nearestHealthTile = gameMap.getUpdatedMap().get(healthPosition).getTile();
        nearestHealthNode = new Node(healthPosition.x, healthPosition.y, nearestHealthTile);
        return nearestHealthNode;
    }

    @Override
    public void decideAction(MyAIController carController) {
        //TODO : abstract this
        Coordinate currentPosition = new Coordinate(carController.getCurrentPosition().x,carController.getCurrentPosition().y);
        MapTile carCurrentPositionTile = carController.getLatestGameMap().getUpdatedMap().get(currentPosition).getTile();
        carCurrentNode = new Node(currentPosition.x, currentPosition.y, carCurrentPositionTile);

        CarControllerStrategy.carControllerActions nextState = null;

        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }
}

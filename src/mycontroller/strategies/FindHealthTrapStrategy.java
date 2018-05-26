package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.Node;
import mycontroller.AStarSearch;
import mycontroller.StrategyControllerRelay;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.List;

public class FindHealthTrapStrategy extends GoalCompletionStrategy {

    private Node nearestHealthNode;

    public FindHealthTrapStrategy(MyAIController c) {

        nearestHealthNode = new Node(c.getLatestGameMap().getNearestHealthTile().x, c.getLatestGameMap().getNearestHealthTile().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getLatestGameMap().getNearestHealthTile().x,
                        c.getLatestGameMap().getNearestHealthTile().y)).getTile());
        startAStarSearch(nearestHealthNode, c);
        currentOrientation = c.getOrientation();
        routeInterpretor(path, currentOrientation);
    }

    @Override
    public void decideAction(MyAIController carController) {
        //TODO : abstract this
        Coordinate currentPosition = new Coordinate(carController.getCurrentPosition().x ,carController.getCurrentPosition().y);
        MapTile carCurrentPositionTile = carController.getLatestGameMap().getUpdatedMap().get(currentPosition).getTile();
        carCurrentNode = new Node(currentPosition.x, currentPosition.y, carCurrentPositionTile);

        CarControllerStrategy.carControllerActions nextState = null;

        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }
}

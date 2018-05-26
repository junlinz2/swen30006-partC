package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.Node;
import mycontroller.AStarSearch;
import mycontroller.StrategyControllerRelay;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.HashMap;
import java.util.List;

public class FindHealthTrapStrategy extends CarNavigationStrategy {

    private Node carCurrentPositionTile;
    private Node nearestHealthNode;
    private AStarSearch aStar;
    private List<Node> path;

    public FindHealthTrapStrategy(StrategyFactory s, MyAIController c) {

        //TODO TEST ASTAR
        carCurrentPositionTile = new Node(c.getCurrentPosition().x, c.getCurrentPosition().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getCurrentPosition().x,c.getCurrentPosition().y)).getTile());
        nearestHealthNode = new Node(c.getLatestGameMap().getNearestHealthTile().x, c.getLatestGameMap().getNearestHealthTile().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getLatestGameMap().getNearestHealthTile().x,
                        c.getLatestGameMap().getNearestHealthTile().y)).getTile());
        aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, carCurrentPositionTile, nearestHealthNode,
                c.getLatestGameMap().getUpdatedMap());
    }

    @Override
    public void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

        //
        CarNavigationStrategy.carControllerActions nextState = null;

        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }
}

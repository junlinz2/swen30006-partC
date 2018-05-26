package mycontroller.strategies;

import mycontroller.AStarSearch;
import mycontroller.MyAIController;
import mycontroller.Node;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class FindKeyStrategy extends CarNavigationStrategy {

    private Node carCurrentPositionTile;
    private Node keyPosition;
    private AStarSearch aStar;

    public FindKeyStrategy(MyAIController c) {
        carCurrentPositionTile = new Node(c.getCurrentPosition().x, c.getCurrentPosition().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getCurrentPosition().x,c.getCurrentPosition().y)).getTile());
        aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, carCurrentPositionTile, ,
                c.getLatestGameMap().getUpdatedMap());
    }

    public Node findNextKey(MyAIController c) {
         //TODO: change for game key getting instead

         keyPosition = new Node(c.getLatestGameMap().getNextKeyCoordinate().x, c.getLatestGameMap().getNextKeyCoordinate().y,
                c.getLatestGameMap().getUpdatedMap().get(new Coordinate(c.getLatestGameMap().getNextKeyCoordinate().x,
                        c.getLatestGameMap().getNextKeyCoordinate().y)).getTile());

        return null;
    }

    // TODO: stub
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                             Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }
}

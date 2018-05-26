package mycontroller;

import mycontroller.strategies.CarNavigationStrategy;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class HealingStrategy extends CarNavigationStrategy {
    @Override
    public void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

    }

    @Override
    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }

    @Override
    public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return false;
    }

    @Override
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }
}

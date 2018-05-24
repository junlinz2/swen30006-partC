package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.exceptions.StrategyNotFoundException;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Group 39
 * Example of Composite Pattern regarding this particular navigation Strategy. Since going through lava can involve
 * either following the left or right wall, we store both of these strategies locally in order to allow for reusability
 * of these behaviours.
 */
public class GoThroughLavaStrategy extends CarNavigationStrategy {

    private CarNavigationStrategy followLeftWallStrategy;
    private CarNavigationStrategy followRightWallStrategy;

    public GoThroughLavaStrategy(StrategyFactory s, MyAIController c) {
        followLeftWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        followRightWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWRIGHTWALL);
    }

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

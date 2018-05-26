package mycontroller.strategies;

import mycontroller.MyAIController;
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
public class GoThroughLavaStrategy extends PathFindingStrategy {

    private PathFindingStrategy followLeftWallStrategy;
    private PathFindingStrategy followRightWallStrategy;

    public GoThroughLavaStrategy(StrategyFactory s, MyAIController c) {
        //followLeftWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        //followRightWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWRIGHTWALL);
    }

    public void decideAction(MyAIController carController) {

    }

    @Override
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                             Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }
}

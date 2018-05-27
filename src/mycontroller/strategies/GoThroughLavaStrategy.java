package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.StrategyControllerRelay;
import tiles.LavaTrap;
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
        followLeftWallStrategy = (PathFindingStrategy) s.createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        followRightWallStrategy = (PathFindingStrategy) s.createCarStrategy(c, MyAIController.strategies.FOLLOWRIGHTWALL);

        tilesToAvoid = new ArrayList<>();
        tilesToAvoid.add(new LavaTrap());
    }

    public void decideAction(MyAIController carController) {

        CarControllerStrategy.carControllerActions nextState = null;

        // New action is relayed by the StrategyControllerRelay singleton to MyAIController
        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }

    @Override
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                             Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }
}

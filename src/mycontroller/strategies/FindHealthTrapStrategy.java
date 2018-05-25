package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.StrategyControllerRelay;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class FindHealthTrapStrategy extends CarNavigationStrategy {

    private CarNavigationStrategy followLeftWallStrategy;
    private CarNavigationStrategy followRightWallStrategy;

    public FindHealthTrapStrategy(StrategyFactory s, MyAIController c) {
        followLeftWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWLEFTWALL);
        followRightWallStrategy = s.createCarStrategy(c, MyAIController.strategies.FOLLOWRIGHTWALL);
    }

    private final int CAR_MAX_HEALTH = 100;

    @Override
    public void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

        CarNavigationStrategy.carControllerActions nextState;

        if (carController.getHealth() == CAR_MAX_HEALTH) {
            nextState = carControllerActions.ACCELERATE;
        }
        else {
            nextState = carControllerActions.SLOWDOWN;
        }

        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }

    @Override
    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }

    @Override
    public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                              Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return false;
    }

    @Override
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                             Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return false;
    }
}

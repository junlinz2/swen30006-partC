package mycontroller.strategies.unused;

import mycontroller.strategies.CarNavigationStrategy;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

public class FollowRightWallStrategy extends CarNavigationStrategy {

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView) {

    }

    /**
     * Check if the wall is on your left hand side given your orientation
     * @param orientation
     * @param currentView
     * @return
     */
    @Override
    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          Coordinate currentPosition) {
        return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.RIGHT, currentPosition);
    }
}

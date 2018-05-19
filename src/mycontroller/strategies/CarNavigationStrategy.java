package mycontroller.strategies;

import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a reference to it
    protected Sensor sensor;

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView) {
    }

    /**
     * Check if you have a wall in front of you!
     *
     * @param orientation the orientation we are in based on WorldSpatial
     * @param currentView what the car can currently see
     * @return
     */
    public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                                   Coordinate currentPosition);

    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }

    public abstract boolean checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                             MapTile tileToDetect);
}

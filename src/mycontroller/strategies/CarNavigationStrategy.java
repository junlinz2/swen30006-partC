package mycontroller.strategies;

import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a reference to it
    Sensor sensor;

    public abstract void doAction(float delta, HashMap<Coordinate, MapTile> currentView,  boolean isTurningLeft,
                                  boolean isTurningRight);

    public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                                   Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}

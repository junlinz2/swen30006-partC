package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

//TODO document this
public abstract class PathFindingStrategy implements CarControllerStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a reference to it
    protected Sensor sensor;
    protected ArrayList<MapTile> tilesToAvoid;

    public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return sensor.checkViewForTile(orientation, currentView, currentPosition, tilesToCheck);
    }

    public abstract boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                      Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);
}

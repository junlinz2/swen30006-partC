package mycontroller.strategies;

import mycontroller.GameMap;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a
    // reference to it
    protected Sensor sensor;
    protected ArrayList<MapTile> tilesToAvoid;

    public abstract void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController);

    public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation,
                                                   HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

    public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return sensor.checkViewForTile(orientation, currentView, currentPosition, tilesToCheck);
    }

    public enum carControllerActions {
        ACCELERATE, SLOWDOWN, ISTURNINGLEFT, ISTURNINGRIGHT, REVERSE, DONOTHING
    }

    public abstract boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                       Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);

    // TODO: Remove this if not used
    public boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float x, float y) {
        switch (orientation) {
            case WEST:
                return (x - coordinate.x) < 0.475;
            case EAST:
                return (x - coordinate.x) > -0.55;
            case NORTH:
                return (y - coordinate.y) > -0.475;
            case SOUTH:
                return (y - coordinate.y) < 0.7;
        }
        return false;
    }

    public abstract boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                      Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);
    
	public abstract void registerObstaclesToFollow(GameMap gameMap, HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);
	
	public abstract void deregisterFollowedObstacles(GameMap gameMap, HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);
}

package mycontroller.strategies;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a reference to it
    private Sensor sensor;
    private ArrayList<MapTile> tilesToAvoid;

    public abstract void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController);

    public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                                   Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

    public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return getSensor().checkViewForTile(orientation, currentView, currentPosition, tilesToCheck);
    }

    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public ArrayList<MapTile> getTilesToAvoid() {
        return tilesToAvoid;
    }

    public void setTilesToAvoid(ArrayList<MapTile> tilesToAvoid) {
        this.tilesToAvoid = tilesToAvoid;
    }


    public enum carControllerActions {
        TURNRIGHT, TURNLEFT, ACCELERATE, DECELERATE, STOPTURNINGLEFT, STOPTURNINGRIGHT, ISTURNINGLEFT,
        ISTURNINGRIGHT, REVERSE, DONOTHING
    }

    public abstract boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                       Coordinate currentPosition);

    public abstract boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float xPos, float yPos);

}
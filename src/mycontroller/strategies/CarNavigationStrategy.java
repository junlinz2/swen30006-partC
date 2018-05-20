package mycontroller.strategies;

import controller.CarController;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import mycontroller.StrategyControllerRelay;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {

    // Different strategies manipulate the behaviour of the sensor, so we need a reference to it
    Sensor sensor;
    StrategyControllerRelay relay;
    ArrayList<MapTile> tilesToAvoid;

    public abstract void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController);

    public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                                   Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

    public boolean checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                    Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return sensor.checkViewForTile(orientation, currentView, currentPosition, tilesToCheck);
    }

    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }


    public enum carControllerActions {
        TURNRIGHT, TURNLEFT, ACCELERATE, DECELERATE, STOPTURNINGLEFT, STOPTURNINGRIGHT, ISTURNINGLEFT,
        ISTURNINGRIGHT, REVERSE, DONOTHING
    }
}

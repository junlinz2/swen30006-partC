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

	// Different strategies manipulate the behaviour of the sensor, so we need a
	// reference to it
	protected Sensor sensor;
	protected ArrayList<MapTile> tilesToAvoid;

	public abstract void doAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController);

	public abstract boolean checkFollowingObstacle(WorldSpatial.Direction orientation,
			HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);

	public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		return sensor.checkViewForTile(orientation, currentView, currentPosition, tilesToCheck);
	}

	public enum carControllerActions {
		TURNRIGHT, TURNLEFT, ACCELERATE, SLOWDOWN, STOPTURNINGLEFT, STOPTURNINGRIGHT, ISTURNINGLEFT, ISTURNINGRIGHT, REVERSE, DONOTHING
	}

	public abstract boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToCheck);

	// TODO: Remove if not used
	public abstract boolean checkTileAccuracy(WorldSpatial.Direction orientation, Coordinate coordinate, float x,
			float y);

	public abstract boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid);
}

package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

    public FollowLeftWallStrategy(Sensor sensor, ArrayList<MapTile> tilesToAvoid) {
        super.sensor = sensor;
    }

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, boolean isTurningLeft, boolean isTurningRight) {

    }

    @Override
    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
                currentPosition, tilesToAvoid);
    }

    public boolean checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                    Coordinate currentPosition, MapTile tileToDetect) {
        HashMap<Coordinate, MapTile> view;
        switch (orientation) {
            case EAST:
                view = sensor.getEastView(currentView, currentPosition);

            case NORTH:
                view = sensor.getNorthView(currentView, currentPosition);
            case SOUTH:
                view = sensor.getSouthView(currentView, currentPosition);
            case WEST:
                view = sensor.getWestView(currentView, currentPosition);
            default:
                return false;
        }
    }
}

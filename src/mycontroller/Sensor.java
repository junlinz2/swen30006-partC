package mycontroller;

import controller.CarController;
import tiles.MapTile;

import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Sensor {

    // How many minimum units obstacles are away from the player.
    private int obstacleSensitivity = 2;

    //TODO: remove if Sensor does not need info from controller
    private CarController carController;

    public Sensor(CarController c) {
        carController = c;
    }

    /**
     * Method below just iterates through the list and check in the correct coordinates.
     * i.e. Given your current position is 10,10
     * getEastView will check up to obstacleSensitivity amount of tiles to the right.
     * getWestView will check up to obstacleSensitivity amount of tiles to the left.
     * getNorthView will check up to obstacleSensitivity amount of tiles to the top.
     * getSouthView will check up to obstacleSensitivity amount of tiles below.
     */

    public HashMap<Coordinate, MapTile> getEastView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        HashMap<Coordinate, MapTile> view = new HashMap<>();
        // Check tiles to my right
        for (int i = 0; i <= obstacleSensitivity; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x + i, currentPosition.y);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public HashMap<Coordinate, MapTile> getWestView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        HashMap<Coordinate, MapTile> view = new HashMap<>();
        // Check tiles to my left
        for (int i = 0; i <= obstacleSensitivity; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x - i, currentPosition.y);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public HashMap<Coordinate, MapTile> getNorthView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        HashMap<Coordinate, MapTile> view = new HashMap<>();
        // Check tiles towards the top
        for (int i = 0; i <= obstacleSensitivity; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x, currentPosition.y + i);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public HashMap<Coordinate, MapTile> getSouthView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        HashMap<Coordinate, MapTile> view = new HashMap<>();
        // Check tiles towards the bottom
        for (int i = 0; i <= obstacleSensitivity; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x, currentPosition.y - i);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          WorldSpatial.RelativeDirection direction, Coordinate currentPosition,
                                          ArrayList<MapTile> tilesToCheck) {
        HashMap<Coordinate, MapTile> view = null;
        if (direction == WorldSpatial.RelativeDirection.LEFT) {
            switch (orientation) {
                case EAST:
                    view = getNorthView(currentView, currentPosition);
                    break;
                case NORTH:
                    view = getWestView(currentView, currentPosition);
                    break;
                case SOUTH:
                    view = getEastView(currentView, currentPosition);
                    break;
                case WEST:
                    view = getSouthView(currentView, currentPosition);
                    break;
            }
        }
        else {
            switch (orientation) {
                case EAST:
                    view = getSouthView(currentView, currentPosition);
                    break;
                case NORTH:
                    view = getEastView(currentView, currentPosition);
                    break;
                case SOUTH:
                    view = getWestView(currentView, currentPosition);
                    break;
                case WEST:
                    view = getNorthView(currentView, currentPosition);
                    break;
            }
        }
        for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
            for (MapTile tile : tilesToCheck) {
                if (tile.getType() == MapTile.Type.TRAP &&
                    tileInView.getValue().getType() == MapTile.Type.TRAP) {
                    if (((TrapTile) tileInView.getValue()).getTrap().equals(((TrapTile)tile).getTrap())) {
                        return true;
                    }
                }
                else if (tileInView.getValue().getType().equals(tile.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}

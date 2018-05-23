package mycontroller;

import tiles.MapTile;

import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

public class Sensor {

    // How many minimum units obstacles are away from the player.
    private int obstacleFollowingSensitivity;
    private int distToTurn;
    private int distToSlowDown;

    public Sensor(int obstacleFollowingSensitivity, int distToTurn, int distToSlowDown) {
        this.obstacleFollowingSensitivity = obstacleFollowingSensitivity;
        this.distToTurn = distToTurn;
        this.distToSlowDown = distToSlowDown;
    }

    /**
     * Method below just iterates through the list and check in the correct coordinates.
     * i.e. Given your current position is 10,10
     * getEastView will check up to obstacleFollowingSensitivity amount of tiles to the right.
     * getWestView will check up to obstacleFollowingSensitivity amount of tiles to the left.
     * getNorthView will check up to obstacleFollowingSensitivity amount of tiles to the top.
     * getSouthView will check up to obstacleFollowingSensitivity amount of tiles below.
     */

    public LinkedHashMap<Coordinate, MapTile> getEastView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
        // Check tiles to my right
        for (int i = 1; i <= distToSlowDown; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x + i, currentPosition.y);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public LinkedHashMap<Coordinate, MapTile> getWestView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
        // Check tiles to my left
        for (int i = 1; i <= distToSlowDown; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x - i, currentPosition.y);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public LinkedHashMap<Coordinate, MapTile> getNorthView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
        // Check tiles towards the top
        for (int i = 1; i <= distToSlowDown; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x, currentPosition.y + i);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public LinkedHashMap<Coordinate, MapTile> getSouthView(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
        LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
        // Check tiles towards the bottom
        for (int i = 1; i <= distToSlowDown; i++) {
            Coordinate coordinate = new Coordinate(currentPosition.x, currentPosition.y - i);
            MapTile tile = currentView.get(coordinate);
            view.put(coordinate, tile);
        }
        return view;
    }

    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          WorldSpatial.RelativeDirection direction, Coordinate currentPosition,
                                          ArrayList<MapTile> tilesToCheck) {
        LinkedHashMap<Coordinate, MapTile> view = null;
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
        } else {
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

        // Loops through Map to allow some flexibility in how close Car should be to wall.
        int i = 1;

        for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
            for (MapTile tile : tilesToCheck) {
                if (tile.getType() == MapTile.Type.TRAP &&
                        tileInView.getValue().getType() == MapTile.Type.TRAP) {
                    if (((TrapTile) tileInView.getValue()).getTrap().equals(((TrapTile) tile).getTrap()) &&
                            i <= obstacleFollowingSensitivity) {
                        return true;
                    }
                } else if (tileInView.getValue().getType().equals(tile.getType()) && i <= obstacleFollowingSensitivity) {
                    return true;
                }
            }
            i++;
            if (i > obstacleFollowingSensitivity) {
                break;
            }
        }
        return false;
    }

    /**
     * Returns how close the nearest tile in tilesToCheck is to the car.
     */
    public int checkViewForTile(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        LinkedHashMap<Coordinate, MapTile> view = null;
        switch (orientation) {
            case EAST:
                view = getEastView(currentView, currentPosition);
                break;
            case NORTH:
                view = getNorthView(currentView, currentPosition);
                break;
            case SOUTH:
                view = getSouthView(currentView, currentPosition);
                break;
            case WEST:
                view = getWestView(currentView, currentPosition);
                break;
        }

        int i = 1;

        for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
            for (MapTile tile : tilesToCheck) {
                if (tile.getType() == MapTile.Type.TRAP &&
                        tileInView.getValue().getType() == MapTile.Type.TRAP) {
                    if (((TrapTile) tileInView.getValue()).getTrap().equals(((TrapTile) tile).getTrap())) {
                        return i;
                    }
                } else if (tileInView.getValue().getType() == tile.getType()) {
                    return i;
                }
            }
            i++;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Return true if a peeked is traversable (ie: a road tile) within four tiles away.
     * @param orientation
     * @param currentView
     * @param currentPosition
     * @param direction
     * @return
     */
    public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                              Coordinate currentPosition, WorldSpatial.RelativeDirection direction) {
        LinkedHashMap<Coordinate, MapTile> view = null;
        if (direction == WorldSpatial.RelativeDirection.LEFT) {
            switch (orientation) {
                case EAST:
                    currentPosition.y = currentPosition.y + 1;
                    view = getEastView(currentView, currentPosition);
                    break;
                case NORTH:
                    currentPosition.x = currentPosition.x - 1;
                    view = getNorthView(currentView, currentPosition);
                    break;
                case SOUTH:
                    currentPosition.x = currentPosition.x + 1;
                    view = getSouthView(currentView, currentPosition);
                    break;
                case WEST:
                    currentPosition.y = currentPosition.y - 1;
                    view = getWestView(currentView, currentPosition);
                    break;
            }
        } else {
            switch (orientation) {
                case EAST:
                    currentPosition.y = currentPosition.y - 1;
                    view = getEastView(currentView, currentPosition);
                    break;
                case NORTH:
                    currentPosition.x = currentPosition.x + 1;
                    view = getNorthView(currentView, currentPosition);
                    break;
                case SOUTH:
                    currentPosition.x = currentPosition.x - 1;
                    view = getSouthView(currentView, currentPosition);
                    break;
                case WEST:
                    currentPosition.y = currentPosition.y + 1;
                    view = getWestView(currentView, currentPosition);
                    break;
            }
        }
        for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
            if (tileInView.getValue() != null && tileInView.getValue().getType() == MapTile.Type.ROAD) {
                return true;
            }
        }
        return false;
    }

    public Coordinate getCornerTileCoordinate(WorldSpatial.Direction orientation, Coordinate currentPosition,
                                              WorldSpatial.RelativeDirection direction) {
        if (direction == WorldSpatial.RelativeDirection.LEFT) {
            switch (orientation) {
                case EAST:
                    currentPosition.y = currentPosition.y + 1;
                    break;
                case NORTH:
                    currentPosition.x = currentPosition.x - 1;
                    break;
                case SOUTH:
                    currentPosition.x = currentPosition.x + 1;
                    break;
                case WEST:
                    currentPosition.y = currentPosition.y - 1;
                    break;
            }
        } else {
            switch (orientation) {
                case EAST:
                    currentPosition.y = currentPosition.y - 1;
                    break;
                case NORTH:
                    currentPosition.x = currentPosition.x + 1;
                    break;
                case SOUTH:
                    currentPosition.x = currentPosition.x - 1;
                    break;
                case WEST:
                    currentPosition.y = currentPosition.y + 1;
                    break;
            }
        }
        return currentPosition;
    }

    public int getDistToTurn() {
        return distToTurn;
    }

    public int getObstacleFollowingSensitivity() {
        return obstacleFollowingSensitivity;
    }

    public int getDistToSlowDown() {
        return distToSlowDown;
    }
}
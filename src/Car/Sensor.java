package Car;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class Sensor {

    // How many minimum units the wall is away from the player.
    private int obstacleSensitivity = 3;

    /**
     * Method below just iterates through the list and check in the correct coordinates.
     * i.e. Given your current position is 10,10
     * checkEast will check up to obstacleSensitivity amount of tiles to the right.
     * checkWest will check up to obstacleSensitivity amount of tiles to the left.
     * checkNorth will check up to obstacleSensitivity amount of tiles to the top.
     * checkSouth will check up to obstacleSensitivity amount of tiles below.
     */
    public boolean checkEast(HashMap<Coordinate, MapTile> currentView) {
        // Check tiles to my right
        Coordinate currentPosition = new Coordinate(getPosition());
        for (int i = 0; i <= obstacleSensitivity; i++) {
            MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
            if (tile.isType(MapTile.Type.WALL)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkWest(HashMap<Coordinate, MapTile> currentView) {
        // Check tiles to my left
        Coordinate currentPosition = new Coordinate(getPosition());
        for (int i = 0; i <= obstacleSensitivity; i++) {
            MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
            if (tile.isType(MapTile.Type.WALL)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNorth(HashMap<Coordinate, MapTile> currentView, ArrayList<String> tilesToAvoid) {
        // Check tiles to towards the top

        Coordinate currentPosition = new Coordinate(getPosition());
        for (int i = 0; i <= obstacleSensitivity; i++) {
            MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
            if (tile.isType(MapTile.Type.WALL)) {
                return true;
            }

            for (String s : tilesToAvoid) {
                System.out.println(tile.getClass().getName());
                if (tile.getClass().getName().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkSouth(HashMap<Coordinate, MapTile> currentView) {
        // Check tiles towards the bottom
        Coordinate currentPosition = new Coordinate(getPosition());
        for (int i = 0; i <= obstacleSensitivity; i++) {
            MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
            if (tile.isType(MapTile.Type.WALL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if you have a wall in front of you!
     * @param orientation the orientation we are in based on WorldSpatial
     * @param currentView what the car can currently see
     * @return
     */
    public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
        switch(orientation){
            case EAST:
                return checkEast(currentView);
            case NORTH:
                return checkNorth(currentView);
            case SOUTH:
                return checkSouth(currentView);
            case WEST:
                return checkWest(currentView);
            default:
                return false;

        }
    }

    /**
     * Check if the wall is on your left hand side given your orientation
     * @param orientation
     * @param currentView
     * @return
     */
    public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                      WorldSpatial.RelativeDirection direction) {
        if (direction == WorldSpatial.RelativeDirection.LEFT) {
            switch (orientation) {
                case EAST:
                    return checkNorth(currentView);
                case NORTH:
                    return checkWest(currentView);
                case SOUTH:
                    return checkEast(currentView);
                case WEST:
                    return checkSouth(currentView);
                default:
                    return false;
            }
        }
        else {
            switch (orientation) {
                case EAST:
                    return checkSouth(currentView);
                case NORTH:
                    return checkEast(currentView);
                case SOUTH:
                    return checkWest(currentView);
                case WEST:
                    return checkNorth(currentView);
                default:
                    return false;
            }
        }
    }
}

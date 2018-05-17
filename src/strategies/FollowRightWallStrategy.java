package strategies;

import mycontroller.MyAIController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

public class FollowRightWallStrategy extends CarNavigationStrategy {

    private MyAIController carController;

    public FollowRightWallStrategy(MyAIController c) {
        carController = c;
    }

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView) {

    }

    /**
     * Check if the wall is on your left hand side given your orientation
     * @param orientation
     * @param currentView
     * @return
     */
    public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {

        switch(orientation){
            case EAST:
                return carController.checkSouth(currentView);
            case NORTH:
                return carController.checkEast(currentView);
            case SOUTH:
                return carController.checkWest(currentView);
            case WEST:
                return carController.checkNorth(currentView);
            default:
                return false;
        }
    }
}

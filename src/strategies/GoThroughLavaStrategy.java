package strategies;

import mycontroller.MyAIController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

public class GoThroughLavaStrategy extends CarNavigationStrategy {

    private MyAIController carController;

    public GoThroughLavaStrategy(MyAIController c) {
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
    //TODO change the logic for this
    public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
        switch(orientation){
            case EAST:
                return carController.checkNorth(currentView);
            case NORTH:
                return carController.checkWest(currentView);
            case SOUTH:
                return carController.checkEast(currentView);
            case WEST:
                return carController.checkSouth(currentView);
            default:
                return false;
        }
    }
}

package mycontroller.strategies;

import controller.CarController;
import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowLeftWallStrategy extends CarNavigationStrategy {

    public FollowLeftWallStrategy(Sensor s, CarController c, ArrayList<MapTile> tilesToAvoid) {
        super.sensor = s;
        super.relay = new StrategyControllerRelay(c);
        super.tilesToAvoid = tilesToAvoid;
    }

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, boolean isTurningLeft,
                         boolean isTurningRight) {

        if(isTurningRight){
            applyRightTurn(getOrientation(),delta);
        }

        // TODO: tweaking strategy for FollowRightWallStrategy.
        else if(isTurningLeft){
            // Apply the left turn if you are not currently near a wall.
            if(!checkFollowingObstacle(getOrientation(), currentView)){
                applyLeftTurn(getOrientation(),delta);
            }
            else{
                isTurningLeft = false;
            }
        }

        // Try to determine whether or not the car is next to a wall.
        else if(carNavigationStrategy.checkFollowingObstacle(super.getOrientation(), currentView, currentPosition)){
            // Maintain some velocity
            if(getSpeed() < CAR_SPEED){
                applyForwardAcceleration();
            }
            // If there is wall ahead, turn right!
            if(sensor.checkViewForTile(getOrientation(), currentView, getPosition())){
                lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
                isTurningRight = true;

            }
        }
        // This indicates that I can do a left turn if I am not turning right
        else{
            lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
            isTurningLeft = true;
        }
    }

    @Override
    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.LEFT,
                currentPosition, tilesToAvoid);
    }
}

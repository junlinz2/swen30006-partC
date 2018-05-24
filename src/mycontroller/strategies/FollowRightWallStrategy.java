package mycontroller.strategies;

import mycontroller.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import java.util.*;

public class FollowRightWallStrategy extends CarNavigationStrategy {

    public FollowRightWallStrategy(MyAIController c) {
        sensor = new Sensor(c.OBSTACLE_FOLLOWING_SENSITIVITY, c.DISTANCE_TO_SLOW_DOWN);
        this.tilesToAvoid = c.getTilesToAvoid();
    }

    public void decideAction(float delta, HashMap<Coordinate, MapTile> currentView, MyAIController carController) {

        CarNavigationStrategy.carControllerActions nextState;

        // Try to determine whether or not the car is next to a wall.
        if (checkFollowingObstacle(carController.getOrientation(), currentView, carController.getCurrentPosition(),
                carController.getTilesToAvoid())) {
            if (carController.justChangedState()) {
                carController.setJustChangedState(false);
            }

            int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
                    carController.getCurrentPosition(), carController.getTilesToAvoid());

            // If there is wall ahead, turn right!
            if (distToObstacle <= carController.DISTANCE_TO_TURN) {
                nextState = carControllerActions.ISTURNINGLEFT;
            }

            // Slow down the car when it's going to turn soon
            else if (distToObstacle <= sensor.getDistToSlowDown() || peekCorner(carController.getOrientation(),
                    currentView, carController.getCurrentPosition(), carController.getTilesToAvoid())) {
                nextState = carControllerActions.SLOWDOWN;
            }

            else {
                nextState = carControllerActions.ACCELERATE;
            }
        }

        // Ensure the car can find a new wall/lava to follow after turning right by
        // keeping it driving along the new orientation
        else if (carController.justChangedState()
                && carController.getLastTurnDirection() == WorldSpatial.RelativeDirection.RIGHT) {
            nextState = carControllerActions.SLOWDOWN;
        }

        // This indicates that I can do a left turn if the car is no longer cruising
        // along an obstacle
        else {
            // Turn left if the car is not turning into a deadend
            if (!isDeadEnd(carController.getOrientation(), currentView, carController.getCurrentPosition(),
                    carController.getTilesToAvoid())) {
                nextState = carControllerActions.ISTURNINGRIGHT;
            }

            // If it's a deadend, keep driving in the current orientation until the next
            // turn
            else {
                int distToObstacle = checkViewForTile(carController.getOrientation(), currentView,
                        carController.getCurrentPosition(), carController.getTilesToAvoid());

                if (distToObstacle <= carController.DISTANCE_TO_TURN) {
                    nextState = carControllerActions.ISTURNINGLEFT;
                }

                else if (distToObstacle <= sensor.getDistToSlowDown()) {
                    nextState = carControllerActions.SLOWDOWN;
                }

                else {
                    nextState = carControllerActions.ACCELERATE;
                }
            }
        }

        // TODO: remove debug statement
        System.out.println(nextState);

        // New action is relayed by the StrategyControllerRelay singleton to
        // MyAIController
        StrategyControllerRelay.getInstance().changeState(carController, nextState);
    }

    public boolean checkFollowingObstacle(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                                          Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return sensor.checkFollowingObstacle(orientation, currentView, WorldSpatial.RelativeDirection.RIGHT,
                currentPosition, tilesToAvoid);
    }

    @Override
    public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                              Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
        return sensor.peekCorner(orientation, currentView, currentPosition, WorldSpatial.RelativeDirection.RIGHT,
                tilesToCheck);
    }

    @Override
    public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
                             Coordinate currentPosition, ArrayList<MapTile> tilesToAvoid) {
        return sensor.isDeadEnd(orientation, currentView, WorldSpatial.RelativeDirection.RIGHT, currentPosition,
                tilesToAvoid);
    }
}
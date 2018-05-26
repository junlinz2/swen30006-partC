package mycontroller.strategies;

import mycontroller.AStarSearch;
import mycontroller.HashMapTile;
import mycontroller.MyAIController;
import mycontroller.Node;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.List;

public abstract class GoalCompletionStrategy implements CarControllerStrategy {

    protected Node carCurrentNode;
    protected WorldSpatial.Direction currentOrientation;
    protected List<Node> path;
    protected List<GoalCompletionStrategy.Movement> movement;

    public abstract void decideAction(MyAIController carController);
    
    public enum Movement {STRAIGHT, LEFT, RIGHT}

    public void startAStarSearch(Node targetNode, MyAIController carController) {
        HashMap<Coordinate, HashMapTile> updateMap = carController.getLatestGameMap().getUpdatedMap();
        int carX = carController.getCurrentPosition().x;
        int carY = carController.getCurrentPosition().y;
        carCurrentNode = new Node(carX, carY, updateMap.get(new Coordinate(carX, carY)).getTile());
        AStarSearch aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, carCurrentNode, targetNode, updateMap);
        path = aStar.findPath();
    }

    public void routeInterpretor(List<Node> path, WorldSpatial.Direction orientation) {
        while (path.size() > 1) {
            Node startPosition = path.get(0);
            Node nextPosition = path.get(1);
            movement.add(checkNextMove(startPosition, nextPosition));
            path.remove(0);
        }
    }

    public GoalCompletionStrategy.Movement checkNextMove(Node startPosition, Node nextPosition) {
        int xChanges = startPosition.getX() - nextPosition.getX();
        int yChanges = startPosition.getY() - nextPosition.getY();
        switch (currentOrientation) {
            case SOUTH:
                if (xChanges < 0) {
                    // turn right
                    currentOrientation = WorldSpatial.Direction.WEST;
                    return GoalCompletionStrategy.Movement.RIGHT;
                } else if (xChanges > 0) {
                    // turnleft
                    currentOrientation = WorldSpatial.Direction.EAST;
                    return GoalCompletionStrategy.Movement.LEFT;
                } else {
                    // straight
                    return GoalCompletionStrategy.Movement.STRAIGHT;
                }
            case NORTH:
                if (xChanges < 0) {
                    // turn left
                    currentOrientation = WorldSpatial.Direction.WEST;
                    return GoalCompletionStrategy.Movement.LEFT;
                } else if (xChanges > 0) {
                    // turnright
                    currentOrientation = WorldSpatial.Direction.EAST;
                    return GoalCompletionStrategy.Movement.RIGHT;
                } else {
                    // straight
                    return GoalCompletionStrategy.Movement.STRAIGHT;
                }
            case EAST:
                if (yChanges < 0) {
                    // left
                    currentOrientation = WorldSpatial.Direction.SOUTH;
                    return GoalCompletionStrategy.Movement.RIGHT;
                } else if (yChanges > 0) {
                    // right
                    currentOrientation = WorldSpatial.Direction.NORTH;
                    return GoalCompletionStrategy.Movement.LEFT;
                } else {
                    // straight
                    return GoalCompletionStrategy.Movement.STRAIGHT;
                }
            case WEST:
                if (yChanges < 0) {
                    // left
                    currentOrientation = WorldSpatial.Direction.SOUTH;
                    return GoalCompletionStrategy.Movement.LEFT;
                } else if (yChanges > 0) {
                    // right
                    currentOrientation = WorldSpatial.Direction.NORTH;
                    return GoalCompletionStrategy.Movement.RIGHT;
                } else {
                    // straight
                    return GoalCompletionStrategy.Movement.STRAIGHT;
                }
        }
        return null;
    }
}

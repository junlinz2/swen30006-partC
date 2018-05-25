package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.CarController;
import mycontroller.strategies.*;
import mycontroller.strategies.AStarSearch;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;

public class MyAIController extends CarController {

	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	private Coordinate currentPosition;
	
	// Keeps track of the previous state
	private WorldSpatial.Direction previousState = null; 
	private boolean justChangedState = false;
	private ArrayList<MapTile> tilesToAvoid = new ArrayList<>();
	private GameMap latestGameMap;

	// Car Speed to move at
	public final float MAX_CAR_SPEED = 3;
	public final float MAX_TURNING_SPEED = 1.4f;
//	public final float MIN_CAR_SPEED = 1f;
  
	public final float MIN_ROTATING_SPEED = 0.5f;
	public final float MIN_CORNER_SPEED = 1.15f;

	// TODO : use a different turning strategy for different corner tile types.
	public final int OBSTACLE_FOLLOWING_SENSITIVITY = 2;
	public final int DISTANCE_TO_TURN = 1;
	public final int DISTANCE_TO_SLOW_DOWN = getViewSquare();

    public float getPreviousHealth() {
        return previousHealth;
    }

    private float previousHealth = 0;
	public final int HEALING_THRESHOLD = 50;

	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;
    private CarNavigationStrategy carNavigationStrategy;

    private StrategyFactory strategyFactory;

    public void setPreviousHealth(float previousHealth) {
        this.previousHealth = previousHealth;
    }

    public enum strategies {FOLLOWLEFTWALL, FOLLOWRIGHTWALL, GOTHROUGHLAVA, HEALING, FINDKEY}

    //TODO TEST ASTAR
    private Node initialNode;
    private Node finalNode;
    private AStarSearch aStar;

	public MyAIController(Car car) {
		super(car);
		tilesToAvoid.add(new MapTile(MapTile.Type.WALL));
		tilesToAvoid.add(new LavaTrap());
		latestGameMap = new GameMap(getMap(), getKey()-1);

        //TODO TEST ASTAR
        initialNode = new Node(1, 1, latestGameMap.getUpdatedMap().get(new Coordinate(1,1)).getTile());
        finalNode = new Node(40, 8, latestGameMap.getUpdatedMap().get(new Coordinate(40,8)).getTile());
        aStar = new AStarSearch(World.MAP_WIDTH, World.MAP_HEIGHT, initialNode, finalNode,
                latestGameMap.getUpdatedMap());
        List<Node> path = aStar.findPath();
        
		/** default to following left wall when simulation starts **/
        strategyFactory = new StrategyFactory(this);
        carNavigationStrategy = strategyFactory.createCarStrategy(this, strategies.FOLLOWLEFTWALL);
	}

    @Override
    public void update(float delta) {
        //TODO print statement here
        //System.out.println(getFloatX() + " " + getFloatY());

        // Gets what the car can see
        HashMap<Coordinate, MapTile> currentView = getView();
        currentPosition = updateCoordinate();
        latestGameMap.updateMap(currentView);
        checkStateChange();

        //TODO remove if unused
        // x = getX();
        // y = getY();

        // If you are not following a wall initially, find a wall to stick to!
        if (!isFollowingWall) {
            if (getSpeed() < MAX_CAR_SPEED) {
                applyForwardAcceleration();
            }
            // Turn towards the north
            if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
                applyLeftTurn(getOrientation(), delta);
            }

            int distToObstacleAhead = carNavigationStrategy.checkViewForTile(WorldSpatial.Direction.NORTH, currentView,
                    currentPosition, tilesToAvoid);

            if (distToObstacleAhead <= DISTANCE_TO_SLOW_DOWN && distToObstacleAhead > DISTANCE_TO_TURN) {
                if (getSpeed() > MAX_TURNING_SPEED)
                    applyReverseAcceleration();
            }

            if (distToObstacleAhead == DISTANCE_TO_TURN) {
                // Turn right until we go back to east!
                if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
                    applyRightTurn(getOrientation(), delta);
                } else {
                    isFollowingWall = true;
                }
            }
        }

        // Once the car is already stuck to a wall, apply the following logic
        else {
            strategyFactory.decideStrategy(this, delta);
            // Readjust the car if it is misaligned.
            readjust(getLastTurnDirection(), delta);

            if (getIsTurningRight()) {
                applyRightTurn(getOrientation(), delta);
            }
            else if (getIsTurningLeft()) {
                applyLeftTurn(getOrientation(), delta);
            }

            carNavigationStrategy.decideAction(delta, currentView, this);
        }
    }


    /**
     * Note: Trying implementing moving away from wall if crashed Readjust the
     * car to the orientation we are in.
     *
     * @param lastTurnDirection
     * @param delta
     */
    private void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
        if (lastTurnDirection != null) {
            if (!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)) {
                adjustRight(getOrientation(), delta);
            } else if (!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)) {
                adjustLeft(getOrientation(), delta);
            }
        }
    }

    /**
     * Try to orient myself to a degree that I was supposed to be at if I am
     * misaligned.
     */
    private void adjustLeft(WorldSpatial.Direction orientation, float delta) {

        switch (orientation) {
            case EAST:
                if (getAngle() > WorldSpatial.EAST_DEGREE_MIN + EAST_THRESHOLD) {
                    turnRight(delta);
                }
                break;
            case NORTH:
                if (getAngle() > WorldSpatial.NORTH_DEGREE) {
                    turnRight(delta);
                }
                break;
            case SOUTH:
                if (getAngle() > WorldSpatial.SOUTH_DEGREE) {
                    turnRight(delta);
                }
                break;
            case WEST:
                if (getAngle() > WorldSpatial.WEST_DEGREE) {
                    turnRight(delta);
                }
                break;

            default:
                break;
        }

    }

    private void adjustRight(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (getAngle() > WorldSpatial.SOUTH_DEGREE && getAngle() < WorldSpatial.EAST_DEGREE_MAX) {
                    turnLeft(delta);
                }
                break;
            case NORTH:
                if (getAngle() < WorldSpatial.NORTH_DEGREE) {
                    turnLeft(delta);
                }
                break;
            case SOUTH:
                if (getAngle() < WorldSpatial.SOUTH_DEGREE) {
                    turnLeft(delta);
                }
                break;
            case WEST:
                if (getAngle() < WorldSpatial.WEST_DEGREE) {
                    turnLeft(delta);
                }
                break;
            default:
                break;
        }

    }

    /**
     * Checks whether the car's state has changed or not, stops turning if it
     * already has.
     */
    private void checkStateChange() {
        if (previousState == null) {
            previousState = getOrientation();
        } else {
            if (previousState != getOrientation()) {
                if (isTurningLeft) {
                    isTurningLeft = false;
                }
                if (isTurningRight) {
                    isTurningRight = false;
                }
                previousState = getOrientation();
                setJustChangedState(true);
            }
        }
    }

    /**
     * Turn the car counter clock wise (think of a compass going counter
     * clock-wise)
     */
    public void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                    turnLeft(delta);
                }
                break;
            case NORTH:
                if (!getOrientation().equals(WorldSpatial.Direction.WEST)) {
                    turnLeft(delta);
                }
                break;
            case SOUTH:
                if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    turnLeft(delta);
                }
                break;
            case WEST:
                if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
                    turnLeft(delta);
                }
                break;
            default:
                break;

        }
        if (getSpeed() < MIN_CORNER_SPEED) {
            applyForwardAcceleration();
        }
        else if (getSpeed() > MAX_TURNING_SPEED) {
            applyReverseAcceleration();
        }
    }

    /**
     * Turn the car clock wise (think of a compass going clock-wise)
     */
    public void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
        switch (orientation) {
            case EAST:
                if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
                    turnRight(delta);
                }
                break;
            case NORTH:
                if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
                    turnRight(delta);
                }
                break;
            case SOUTH:
                if (!getOrientation().equals(WorldSpatial.Direction.WEST)) {
                    turnRight(delta);
                }
                break;
            case WEST:
                if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
                    turnRight(delta);
                }
                break;
            default:
                break;
        }
        if (getSpeed() > MAX_TURNING_SPEED) {
            applyReverseAcceleration();
        }
        //else if (carController.getSpeed() < carController.getMinCarSpeed()) {
        else if (getSpeed() < MIN_ROTATING_SPEED) {
            applyForwardAcceleration();
        }
    }

    public boolean justChangedState() {
        return justChangedState;
    }

    public void setCarNavigationStrategy(CarNavigationStrategy strategy) {
        this.carNavigationStrategy = strategy;
    }

    public boolean getIsTurningLeft() {
        return isTurningLeft;
    }

    public boolean getIsTurningRight() {
        return isTurningRight;
    }

    public void setTurningLeft(boolean turningLeft) {
        isTurningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        isTurningRight = turningRight;
    }

    public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
        this.lastTurnDirection = lastTurnDirection;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    private Coordinate updateCoordinate() {
        return new Coordinate(getPosition());
    }

    public ArrayList<MapTile> getTilesToAvoid() {
        return tilesToAvoid;
    }

    //TODO Remove if unused
//	public float getFloatX() {
//		return getX();
//	}
//
//	public float getFloatY() {
//		return getY();
//	}

    public void setJustChangedState(boolean justChangedState) {
        this.justChangedState = justChangedState;
    }

    public WorldSpatial.RelativeDirection getLastTurnDirection() {
        return lastTurnDirection;
    }
}

package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import controller.CarController;
import mycontroller.strategies.CarNavigationStrategy;
import mycontroller.strategies.FollowLeftWallStrategy;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import Car.*;
import world.WorldSpatial;

public class MyAIController extends CarController {

	private boolean isFollowingWall = false; // This is initialized when the car
												// sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows
																		// the
																		// last
																		// turn
																		// direction
																		// the
																		// car
																		// takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;

	private Coordinate currentPosition;

	private WorldSpatial.Direction previousState = null; // Keeps track of the
															// previous state

	private ArrayList<MapTile> tilesToAvoid = new ArrayList<>();
	private CarNavigationStrategy carNavigationStrategy;

	// Car Speed to move at
	private final float MAX_CAR_SPEED = 3;
	private final float MAX_TURNING_SPEED = 0.8f;
	private final float MIN_CAR_SPEED = 0.5f;

	// Slow down and turning threshold when preparing to turn
	// private int turnThreshold = 2;
	// private int slowDownThreshold = 4;

	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;

	public MyAIController(Car car) {
		super(car);
		tilesToAvoid.add(new MapTile(MapTile.Type.WALL));
		tilesToAvoid.add(new LavaTrap());// TODO maybe don't use a magic string
											// here

		/** default to following left wall when simulation starts **/
		carNavigationStrategy = new FollowLeftWallStrategy(this, tilesToAvoid);
	}

	@Override
	public void update(float delta) {

		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		currentPosition = updateCoordinate();

		checkStateChange();

		// If you are not following a wall initially, find a wall to stick to!
		if (!isFollowingWall) {
			if (getSpeed() < MAX_CAR_SPEED) {
				applyForwardAcceleration();
			}
			// Turn towards the north
			if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				applyLeftTurn(getOrientation(), delta);
			}
			
			if (carNavigationStrategy.checkViewForTile(WorldSpatial.Direction.NORTH, currentView, currentPosition,
					tilesToAvoid) <= 4 && carNavigationStrategy.checkViewForTile(WorldSpatial.Direction.NORTH, currentView, currentPosition, tilesToAvoid) >1) {
				if(getSpeed() > MAX_TURNING_SPEED)
				applyReverseAcceleration();
			}

			// TODO: magic number here "1"
			if (carNavigationStrategy.checkViewForTile(WorldSpatial.Direction.NORTH, currentView, currentPosition,
					tilesToAvoid) == 1) {
				// Turn right until we go back to east!
				if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					applyRightTurn(getOrientation(), delta);
				} else {
					isFollowingWall = true;
				}
			}

		}

		// Once the car is already stuck to a wall, apply the following logic
		else {

			// Readjust the car if it is misaligned.
			readjust(lastTurnDirection, delta);

			carNavigationStrategy.doAction(delta, currentView, this);
		}
	}

	/**
	 * Readjust the car to the orientation we are in.
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

	public float getMaxCarSpeed() {
		return MAX_CAR_SPEED;
	}

	public float getMinCarSpeed() {
		return MIN_CAR_SPEED;
	}

	public float getMaxTurningSpeed() {
		return MAX_TURNING_SPEED;
	}

}

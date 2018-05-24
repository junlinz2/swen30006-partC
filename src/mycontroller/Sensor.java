package mycontroller;

import tiles.MapTile;

import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

public class Sensor {
	// How many minimum units obstacles are away from the player.
	private int obstacleFollowingSensitivity;
	private int distToSlowDown;

	public Sensor(int obstacleFollowingSensitivity, int distToTurn, int distToSlowDown) {
		this.obstacleFollowingSensitivity = obstacleFollowingSensitivity;
		this.distToSlowDown = distToSlowDown;
	}

	/**
	 * Method below just iterates through the list and check in the correct
	 * coordinates. i.e. Given your current position is 10,10 getEastView will
	 * check up to obstacleFollowingSensitivity amount of tiles to the right.
	 * getWestView will check up to obstacleFollowingSensitivity amount of tiles
	 * to the left. getNorthView will check up to obstacleFollowingSensitivity
	 * amount of tiles to the top. getSouthView will check up to
	 * obstacleFollowingSensitivity amount of tiles below.
	 */

	//TODO: change distToSlowDown to getViewSquare() (Same thing but naming matters)
	public LinkedHashMap<Coordinate, MapTile> getEastView(HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition) {
		LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
		// Check tiles to my right
		for (int i = 1; i <= distToSlowDown; i++) {
			Coordinate coordinate = new Coordinate(currentPosition.x + i, currentPosition.y);
			MapTile tile = currentView.get(coordinate);
			view.put(coordinate, tile);
		}
		return view;
	}

	public LinkedHashMap<Coordinate, MapTile> getWestView(HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition) {
		LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
		// Check tiles to my left
		for (int i = 1; i <= distToSlowDown; i++) {
			Coordinate coordinate = new Coordinate(currentPosition.x - i, currentPosition.y);
			MapTile tile = currentView.get(coordinate);
			view.put(coordinate, tile);
		}
		return view;
	}

	public LinkedHashMap<Coordinate, MapTile> getNorthView(HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition) {
		LinkedHashMap<Coordinate, MapTile> view = new LinkedHashMap<>();
		// Check tiles towards the top
		for (int i = 1; i <= distToSlowDown; i++) {
			Coordinate coordinate = new Coordinate(currentPosition.x, currentPosition.y + i);
			MapTile tile = currentView.get(coordinate);
			view.put(coordinate, tile);
		}
		return view;
	}

	public LinkedHashMap<Coordinate, MapTile> getSouthView(HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition) {
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
			WorldSpatial.RelativeDirection direction, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {
		LinkedHashMap<Coordinate, MapTile> view = getOrientationView(currentView, orientation, direction,
				currentPosition);

		// Loops through Map to allow some flexibility in how close Car should
		// be to
		// wall.
		int i = 1;
		for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
			for (MapTile tile : tilesToCheck) {
				if (TilesChecker.checkTileTypeSame(tile, tileInView.getValue()) && i <= obstacleFollowingSensitivity)
					return true;
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
				if (TilesChecker.checkTileTypeSame(tile, tileInView.getValue()))
					return i;
			}

			i++;
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Return true if a peeked is traversable (ie: a road tile) within four
	 * tiles away.
	 * 
	 * @param orientation
	 * @param currentView
	 * @param currentPosition
	 * @param direction
	 * @return
	 */
	// TODO: Make use of obstacleFollowingSensitivity
	public boolean peekCorner(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			Coordinate currentPosition, WorldSpatial.RelativeDirection direction, ArrayList<MapTile> tilesToCheck) {
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
			for (MapTile tile : tilesToCheck) {
				if (tileInView.getValue() != null && TilesChecker.checkTileTraversable(tileInView.getValue(), tile)) {
					return true;
				}
			}
		}
		
		return false;
	}

	// TODO:Explain code logic
	// TODO: Implement fucking exception (probably won't have time for that)
	public boolean isDeadEnd(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.RelativeDirection direction, Coordinate currentPosition, ArrayList<MapTile> tilesToCheck) {

		LinkedHashMap<Coordinate, MapTile> view = getOrientationView(currentView, orientation, direction,
				currentPosition);

		for (Map.Entry<Coordinate, MapTile> tileInView : view.entrySet()) {
			for (MapTile tile : tilesToCheck) {
				//if (areTilesSameType(tileInView.getValue(), tile)) {
				if (TilesChecker.checkTileTypeSame(tileInView.getValue(), tile)) {
					Coordinate roadBeforeObstacle = null;
					int obstacleX = tileInView.getKey().x;
					int obstacleY = tileInView.getKey().y;

					switch (orientation) {
					case EAST:
						roadBeforeObstacle = new Coordinate(obstacleX, obstacleY - 1);
						break;
					case NORTH:
						roadBeforeObstacle = new Coordinate(obstacleX + 1, obstacleY);
						break;
					case SOUTH:
						roadBeforeObstacle = new Coordinate(obstacleX - 1, obstacleY);
						break;
					case WEST:
						roadBeforeObstacle = new Coordinate(obstacleX, obstacleY + 1);
						break;
					}

					return isSinglePath(roadBeforeObstacle, orientation, currentView, tilesToCheck);
				}
			}
		}

		// No wallTile found in currentView in the interested direction/path
		// Assume all deadends can be seen in currentView
		return false;
	}

	private boolean isSinglePath(Coordinate roadBeforeObstacle, WorldSpatial.Direction orientation,
			HashMap<Coordinate, MapTile> currentView, ArrayList<MapTile> tilesToCheck) {
		Coordinate adjacentTile1 = null;
		Coordinate adjacentTile2 = null;

		switch (orientation) {
		case NORTH:
		case SOUTH:
			adjacentTile1 = new Coordinate(roadBeforeObstacle.x, roadBeforeObstacle.y + 1);
			adjacentTile2 = new Coordinate(roadBeforeObstacle.x, roadBeforeObstacle.y - 1);
			break;
		case EAST:
		case WEST:
			adjacentTile1 = new Coordinate(roadBeforeObstacle.x + 1, roadBeforeObstacle.y);
			adjacentTile2 = new Coordinate(roadBeforeObstacle.x - 1, roadBeforeObstacle.y);
			break;
		}

		if (adjacentTile1 == null && adjacentTile2 == null) {
			// TODO: Create Exception (Invalid direction)
			return false;
		}

		else {
			boolean istile1Obstacle = false;
			boolean istile2Obstacle = false;

			for (MapTile tile : tilesToCheck) {
				if (TilesChecker.checkTileTypeSame(currentView.get(adjacentTile1), tile)) {
					istile1Obstacle = true;
				}

				if (TilesChecker.checkTileTypeSame(currentView.get(adjacentTile2), tile)) {
					istile2Obstacle = true;
				}
			}

			return istile1Obstacle && istile2Obstacle;
		}
	}

	private LinkedHashMap<Coordinate, MapTile> getOrientationView(HashMap<Coordinate, MapTile> currentView,
			WorldSpatial.Direction orientation, WorldSpatial.RelativeDirection direction, Coordinate currentPosition) {

		if (direction == WorldSpatial.RelativeDirection.LEFT) {
			switch (orientation) {
			case EAST:
				return getNorthView(currentView, currentPosition);
			case NORTH:
				return getWestView(currentView, currentPosition);
			case SOUTH:
				return getEastView(currentView, currentPosition);
			case WEST:
				return getSouthView(currentView, currentPosition);
			}
		} else {
			switch (orientation) {
			case EAST:
				return getSouthView(currentView, currentPosition);
			case NORTH:
				return getEastView(currentView, currentPosition);
			case SOUTH:
				return getWestView(currentView, currentPosition);
			case WEST:
				return getNorthView(currentView, currentPosition);
			}
		}

		return null;
	}

	// TODO: Remove if not used
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

	public int getObstacleFollowingSensitivity() {
		return obstacleFollowingSensitivity;
	}

	public int getDistToSlowDown() {
		return distToSlowDown;
	}
}

package strategies;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

public class FindWallStrategy implements CarNavigationStrategy {
    private boolean findLeftWall;

    public FindWallStrategy(boolean findLeftWall) {
        this.findLeftWall = findLeftWall;
    }

    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView, CarController c) {}
}

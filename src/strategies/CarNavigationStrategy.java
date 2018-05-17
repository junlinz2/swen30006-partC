package strategies;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

//TODO document this
public abstract class CarNavigationStrategy {
    public void doAction(float delta, HashMap<Coordinate, MapTile> currentView) {}

    public abstract boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView);

    public String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}

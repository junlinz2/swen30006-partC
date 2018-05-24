package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;

import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;;

public class DriveableTileChecker {

	public static final ArrayList<Type> drivableTileTypes = new ArrayList<>(
			Arrays.asList(Type.TRAP, Type.ROAD, Type.START, Type.FINISH));
	
	public static final ArrayList<String> drivableTraps = new ArrayList<>(
			Arrays.asList("health", "lava"));
	
	/**
	 * Check if the car can drive on the given tile
	 * First check if the tile is of the same type as tileToAvoid (if yes, means the car cannot drive on it)
	 * Then 
	 * @param tile
	 * @param tileToAvoid
	 * @return
	 */
	public static boolean isTileDrivable(MapTile tile, MapTile tileToAvoid) {
		Type tileType = tile.getType();
		Type tileToAvoidType = tileToAvoid.getType();
		
		//Check if tile and tileToAvoid are of sametype/trap
		if (tileType.equals(tileToAvoidType)) {
			if (tileType.equals(Type.TRAP)) {
				String tileTrap = ((TrapTile) tile).getTrap();
				String tileToAvoidTrap = ((TrapTile) tileToAvoid).getTrap();
				//If tileTrap and tileToAvoidTrap not equal, continue with the next if-block
				if (tileTrap.equals(tileToAvoidTrap)) {
					return false;
				} 
			} else {
				return false;
			}
		}
		
		//Check if tile is one of the drivable types
		if (drivableTileTypes.contains(tileType)) {
			if (tileType.equals(Type.TRAP)) {
				return drivableTraps.contains(((TrapTile) tile).getTrap()) ? true : false;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	
}

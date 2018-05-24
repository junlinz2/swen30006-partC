package mycontroller;

import java.util.ArrayList;
import java.util.Arrays;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;

public class TilesChecker {
	private static ArrayList<String> nameOfTilesWithKeys = new ArrayList<>(Arrays.asList("lava"));

	public static boolean checkTileWithKeys(MapTile tile) {
		if (checkForTrapTile(tile)) {
			return checkTrapTileWithKeys(tile);
		}
		return false;
	}

	public static boolean checkForTrapTile(MapTile tile) {
		return tile.getType() == MapTile.Type.TRAP;
	}

	public static boolean checkTrapTileWithKeys(MapTile tile) {
		return nameOfTilesWithKeys.contains(((TrapTile) tile).getTrap());
	}

	public static int getKeyFromTile(MapTile tile) {
		String trap = ((TrapTile) tile).getTrap();
		switch (trap) {
		case "lava":
			return ((LavaTrap) tile).getKey();

		default:
			return 0;
		}
	}

	public static boolean checkTileIsEmpty(MapTile tile) {
		return tile.getType() == MapTile.Type.EMPTY;
	}

	public static boolean checkTileTypeSame(MapTile tile1, MapTile tile2) {
		if (tile1.getType() == MapTile.Type.TRAP && tile2.getType() == MapTile.Type.TRAP) {
			if (((TrapTile) tile1).getTrap().equals(((TrapTile) tile2).getTrap())) {
				return true;
			}
		} else if (tile1.getType().equals(tile2.getType())) {
			return true;
		}
		return false;
	}
}

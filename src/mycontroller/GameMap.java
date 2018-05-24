package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class GameMap {
	private HashMap<Coordinate, HashMapTile> updatedMap = new HashMap<>();

	public GameMap(HashMap<Coordinate, MapTile> map) {
		createMap(map);
	}

	private void createMap(HashMap<Coordinate, MapTile> map) {
		for (Coordinate key : map.keySet()) {
			System.out.println(map.get(key).getType());
			updatedMap.put(key, new HashMapTile(map.get(key)));
		}
	}

	public void updateMap(HashMap<Coordinate, MapTile> currentView) {
		//iterate currentView
		for (Coordinate key : currentView.keySet()) {
			if(TilesChecker.checkTileIsEmpty(currentView.get(key))){
				break;
			}
			HashMapTile tileFromMap = updatedMap.get(key);
			
			//get from map the object to check if it has been explored
			if (tileFromMap.getExplored() == 0) {
				//get the corresponding tile from view based on coordinate
				MapTile tileFromView = currentView.get(key);

				//check if its a lava trap by using TilesWithKeysChecker class
				if (TilesChecker.checkTileWithKeys(tileFromView)) {
					
					//Assume without key, key value = 0
					//if it contains a key, set key value 
					if (TilesChecker.getKeyFromTile(tileFromView) != 0) {
						tileFromMap.setKeyValue(TilesChecker.getKeyFromTile(tileFromView));
					}
				}
				
				//set explored and change tile type
				tileFromMap.setExplored(1);
				tileFromMap.setTile(tileFromView);
				updatedMap.put(key, tileFromMap);
			}
		}
	}
	
}

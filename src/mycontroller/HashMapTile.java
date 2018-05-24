package mycontroller;

import tiles.MapTile;

public class HashMapTile {
	private MapTile tile;
	private int isExplored;
	private boolean toFollow;
	private int keyValue;

	public HashMapTile (MapTile tile){
		this.tile = tile;
		this.isExplored = 0;
		this.toFollow = false;
		this.keyValue = 0;
	}

	public int getExplored(){
		return isExplored;
	}

	public void setExplored(int keyValue){
		this.isExplored = keyValue;
	}

	public void setTile(MapTile tile){
		this.tile = tile;
	}

	public MapTile getTile(){
		return this.tile;
	}

	public boolean isType(MapTile.Type tileType){
		return this.tile.isType(tileType);
	}

	public boolean isToFollow() {
		return toFollow;
	}

	public void setToFollow(boolean toFollow) {
		this.toFollow = toFollow;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

}

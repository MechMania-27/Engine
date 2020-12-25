package mech.mania.engine.model;

import java.util.List;

public class TileMap {
    private int mapHeight;
    private int mapWidth;
    private List<List<Tile>> tiles;

    public TileMap(int mapHeight, int mapWidth, List<List<Tile>> tiles) {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.tiles = tiles;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }

    public void setTiles(List<List<Tile>> tiles) {
        this.tiles = tiles;
    }
}

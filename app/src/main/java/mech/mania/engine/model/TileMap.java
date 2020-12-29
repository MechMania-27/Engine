package mech.mania.engine.model;

import mech.mania.engine.config.Config;
import mech.mania.engine.util.GameUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMap {
    private final int mapHeight;
    private final int mapWidth;
    private final List<List<Tile>> tiles;
    private final Config gameConfig;
    private final Player player1;
    private final Player player2;

    public TileMap(final Config gameConfig, final Player player1, final Player player2) {
        mapHeight = gameConfig.getBoardHeight();
        mapWidth = gameConfig.getBoardWidth();

        tiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                // TODO: check if correct
                tiles.get(row).add(new Tile(TileType.SOIL, 0));
            }
        }

        this.player1 = player1;
        this.player2 = player2;
        this.gameConfig = gameConfig;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < mapWidth && pos.getY() >= 0 && pos.getY() < mapHeight;
    }

    public void movePlayer1(Position newPos) {
        if (isValidPosition(newPos)) {
            // error handling/notification for invalid position

        }

        if (GameUtils.distance(player1.getPosition(), newPos) < gameConfig.getMaxMovement()) {
            // error handling/notification for moving too far

        }

        player1.setPosition(newPos);
    }

    public void movePlayer2(Position newPos) {
        if (isValidPosition(newPos)) {
            // error handling/notification for invalid position

        }

        if (GameUtils.distance(player2.getPosition(), newPos) < gameConfig.getMaxMovement()) {
            // error handling/notification for moving too far

        }

        player2.setPosition(newPos);
    }

    public Iterator<Tile> iterator() {
        return new TileMapIterator();
    }


    class TileMapIterator implements Iterator<Tile> {
        private int pos = 0;

        // java.util.Iterator methods
        @Override
        public boolean hasNext() {
            return pos < mapHeight * mapWidth;
        }

        @Override
        public Tile next() {
            int row = pos / mapWidth;
            int col = pos % mapWidth;
            pos++;
            return tiles.get(row).get(col);
        }
    }

}


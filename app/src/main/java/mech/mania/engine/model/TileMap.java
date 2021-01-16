package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;
import mech.mania.engine.util.GameUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class TileMap implements Iterable<Tile> {
    @Expose
    private final int mapHeight;
    @Expose
    private final int mapWidth;
    @Expose
    private final ArrayList<ArrayList<Tile>> tiles;

    private final Config gameConfig;
    private final Player player1;
    private final Player player2;

    public TileMap(final Config gameConfig, final Player player1, final Player player2) {
        this.gameConfig = gameConfig;
        mapHeight = gameConfig.BOARD_HEIGHT;
        mapWidth = gameConfig.BOARD_WIDTH;

        tiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                if (row == 0) {
                    tiles.get(row).add(new Tile(TileType.GREEN_GROCER));
                } else {
                    tiles.get(row).add(new Tile(TileType.SOIL));
                }
            }
        }
        setFertilityBand(1);

        this.player1 = player1;
        this.player2 = player2;
    }

    public TileMap(TileMap other) {
        this.mapHeight = other.mapHeight;
        this.mapWidth = other.mapWidth;
        this.tiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                tiles.get(row).add(new Tile(other.tiles.get(row).get(col)));
            }
        }
        this.gameConfig = other.gameConfig;
        this.player1 = new Player(other.player1);
        this.player2 = new Player(other.player2);
    }

    /** Sets the fertility of tiles based on the fertility band's position at a specified turn
     * @param turn The specified turn, where the first turn is 1
     */
    public void setFertilityBand(int turn) {
        int shifts = (turn - 1) / gameConfig.F_BAND_MOVE_DELAY;

        for (int row = 0; row < mapHeight; row++) {
            // Green Grocer tiles are unaffected by the fertility bands
            // if (tile.getType() == TileType.GREEN_GROCER){
            //     continue;
            // }
            if (row == 0) {
                continue;
            }

            TileType tileType;

            // offset records how far into the fertility zone a row is (negative indicates below)
            int offset = shifts - row - 1;
            if(offset < 0){
                // Below fertility band
                tileType = TileType.SOIL;
            }
            else if(offset < gameConfig.F_BAND_OUTER_HEIGHT){
                // Within first outer band
                tileType = TileType.F_BAND_OUTER;
            }
            else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT){
                // Within first mid band
                tileType = TileType.F_BAND_MID;
            }
            else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within inner band
                tileType = TileType.F_BAND_INNER;
            }
            else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within second mid band
                tileType = TileType.F_BAND_MID;
            }
            else if(offset < 2 * gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within second outer band
                tileType = TileType.F_BAND_OUTER;
            }
            else{
                // Above fertility bands
                tileType = TileType.ARID;
            }

            tiles.get(row).forEach(tile -> tile.setType(tileType));
        }
    }

    public boolean isGreenGrocer(Position pos) {
        return isValidPosition(pos) &&
                tiles.get(pos.getY()).get(pos.getX()).getType() == TileType.GREEN_GROCER;
    }

    /** Grows all crops on this TileMap */
    public void growCrops(){
        for (Tile tile : this) {
            Crop crop = tile.getCrop();

            if (crop == null) {
                continue;
            }

            // Only affect crops which are still growing
            if (crop.getType() != CropType.NONE && crop.getGrowthTimer() > 0) {
                // Increase value
                crop.grow(tile.getFertility());
            }

            // Update tile states
            tile.setFertilityIdolEffect(false);
        }
    }

    public void plantCrop(Position pos, CropType type) {
        if (isValidPosition(pos)) {
            tiles.get(pos.getX()).get(pos.getY()).setCrop(new Crop(type));
        }
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

    public TileType getTileType(Position pos) {
        return tiles.get(pos.getX()).get(pos.getY()).getType();
    }

    public Tile getTile(Position pos) {
        if (isValidPosition(pos)) {
            return tiles.get(pos.getX()).get(pos.getY());
        }
        return null;
    }

    public void movePlayer1(Position newPos) {
        if (isValidPosition(newPos)) {
            // error handling/notification for invalid position

        }

        if (GameUtils.distance(player1.getPosition(), newPos) < gameConfig.MAX_MOVEMENT) {
            // error handling/notification for moving too far

        }

        player1.setPosition(newPos);
    }

    public void movePlayer2(Position newPos) {
        if (isValidPosition(newPos)) {
            // error handling/notification for invalid position

        }

        if (GameUtils.distance(player2.getPosition(), newPos) < gameConfig.MAX_MOVEMENT) {
            // error handling/notification for moving too far

        }

        player2.setPosition(newPos);
    }

    @Override
    public Iterator<Tile> iterator() {
        return new TileMapIterator();
    }

    /**
     * Utility Iterator object to iterate through Tiles easily
     */
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


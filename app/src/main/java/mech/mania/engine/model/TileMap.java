package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;
import mech.mania.engine.util.GameUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMap {
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
        mapHeight = gameConfig.BOARD_HEIGHT;
        mapWidth = gameConfig.BOARD_WIDTH;

        tiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                // Actual value irrelevant -- will be set by fertility band afterwards
                tiles.get(row).add(new Tile(TileType.SOIL_0));
            }
        }
        setFertilityBand(1);

        this.player1 = player1;
        this.player2 = player2;
        this.gameConfig = gameConfig;
    }

    public TileMap(TileMap other) {
        this.mapHeight = other.mapHeight;
        this.mapWidth = other.mapWidth;
        this.tiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                tiles.get(row).add(other.tiles.get(row).get(col));
            }
        }
        this.gameConfig = other.gameConfig;
        this.player1 = new Player(other.player1);
        this.player2 = new Player(other.player2);
    }

    /** Sets the fertility of tiles based on the fertility band's position at a specified turn
     * @param turn The specified turn, where the first turn is 1
     */
    public void setFertilityBand(int turn){
        int shifts = (turn - 1) / gameConfig.F_BAND_MOVE_DELAY;

        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                Tile tile = tiles.get(row).get(col);

                // Green Grocer tiles are unaffected by the fertility bands
                if (tile.getType() == TileType.GREEN_GROCER){
                    continue;
                }

                // offset records how far into the fertility zone a row is (negative indicates below)
                int offset = shifts - row - 1;
                if(offset < 0){
                    // Below fertility band
                    tile.setType(TileType.SOIL_0);
                    tile.setFertility(0); // TODO: why not add this to game config?
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT){
                    // Within first outer band
                    tile.setType(TileType.SOIL_1);
                    tile.setFertility(gameConfig.F_BAND_OUTER_FERTILITY);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT){
                    // Within first mid band
                    tile.setType(TileType.SOIL_2);
                    tile.setFertility(gameConfig.F_BAND_MID_FERTILITY);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within inner band
                    tile.setType(TileType.SOIL_3);
                    tile.setFertility(gameConfig.F_BAND_INNER_FERTILITY);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within second mid band
                    tile.setType(TileType.SOIL_2);
                    tile.setFertility(gameConfig.F_BAND_MID_FERTILITY);
                }
                else if(offset < 2 * gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within second outer band
                    tile.setType(TileType.SOIL_1);
                    tile.setFertility(gameConfig.F_BAND_OUTER_FERTILITY);
                }
                else{
                    // Above fertility bands
                    tile.setType(TileType.ARID);
                    tile.setFertility(0);
                }
            }
        }
    }

    /** Grows all crops on this TileMap */
    public void growCrops(){
        Iterator<Tile> iter = this.iterator();
        while(iter.hasNext()){
            Tile tile = iter.next();
            Crop crop = tile.getCrop();

            // Only affect crops which are still growing
            if(crop.getType() != CropType.NONE && crop.getGrowthTimer() > 0) {
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


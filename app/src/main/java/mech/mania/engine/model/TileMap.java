package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMap implements Iterable<Tile> {
    @Expose
    private final int mapHeight;
    @Expose
    private final int mapWidth;
    @Expose
    private final List<List<Tile>> tiles;

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
                tiles.get(row).add(new Tile(TileType.SOIL));
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
    public void setFertilityBand(int turn) {
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
                    tile.setType(TileType.SOIL);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT){
                    // Within first outer band
                    tile.setType(TileType.F_BAND_OUTER);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT){
                    // Within first mid band
                    tile.setType(TileType.F_BAND_MID);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within inner band
                    tile.setType(TileType.F_BAND_INNER);
                }
                else if(offset < gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within second mid band
                    tile.setType(TileType.F_BAND_MID);
                }
                else if(offset < 2 * gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                        gameConfig.F_BAND_INNER_HEIGHT){
                    // Within second outer band
                    tile.setType(TileType.F_BAND_OUTER);
                }
                else{
                    // Above fertility bands
                    tile.setType(TileType.ARID);
                }
            }
        }
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

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < mapWidth && pos.getY() >= 0 && pos.getY() < mapHeight;
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


package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.Iterator;

public class TileMap implements Iterable<Tile> {
    @Expose
    private final int mapHeight;
    @Expose
    private final int mapWidth;
    @Expose
    private final ArrayList<ArrayList<Tile>> tiles;
    @Expose
    private final ArrayList<Position> greenGrocerTiles;

    private static Config gameConfig;
    private final Player player1;
    private final Player player2;

    private static final TileType[] UNPLANTABLE_TILETYPES = {TileType.GREEN_GROCER, TileType.GRASS};

    public TileMap(final Config gameConfig, final Player player1, final Player player2) {
        TileMap.gameConfig = gameConfig;
        mapHeight = gameConfig.BOARD_HEIGHT;
        mapWidth = gameConfig.BOARD_WIDTH;

        tiles = new ArrayList<>();
        greenGrocerTiles = new ArrayList<>();

        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                if (row < gameConfig.GRASS_ROWS) {
                    // Green Grocer tiles are at the top center
                    if (row == 0 && Math.abs(col - mapWidth / 2) <= gameConfig.GREENGROCER_LENGTH / 2) {
                        greenGrocerTiles.add(new Position(col, row));
                        tiles.get(row).add(new Tile(TileType.GREEN_GROCER, gameConfig));
                    } else {
                        tiles.get(row).add(new Tile(TileType.GRASS, gameConfig));
                    }
                } else {
                    tiles.get(row).add(new Tile(TileType.SOIL, gameConfig));
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
        this.greenGrocerTiles = new ArrayList<>();
        for (int row = 0; row < mapHeight; row++) {
            tiles.add(new ArrayList<>());
            for (int col = 0; col < mapWidth; col++) {
                tiles.get(row).add(new Tile(other.tiles.get(row).get(col)));
            }
        }

        for (Position p : other.greenGrocerTiles) {
            this.greenGrocerTiles.add(new Position(p));
        }

        this.player1 = new Player(other.player1);
        this.player2 = new Player(other.player2);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TileMap)) {
            return false;
        }

        TileMap other = (TileMap) obj;
        if (mapHeight != other.mapHeight) return false;
        if (mapWidth != other.mapWidth) return false;
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = 0; j < tiles.get(i).size(); j++) {
                if (!tiles.get(i).get(j).equals(other.tiles.get(i).get(j))) {
                    return false;
                }
            }
        }
        if (!player1.equals(other.player1)) return false;
        if (!player2.equals(other.player2)) return false;

        return true;
    }

    /** Sets the fertility of tiles based on the fertility band's position at a specified turn
     * @param turn The specified turn, where the first turn is 1
     */
    public void setFertilityBand(int turn) {
        int shifts = (turn - 1 - gameConfig.F_BAND_INIT_DELAY) / gameConfig.F_BAND_MOVE_DELAY;
        shifts = Math.max(0, shifts);

        for (int row = 0; row < mapHeight; row++) {
            TileType newType;

            // offset records how far into the fertility zone a row is (negative indicates below)
            // init position indicates the first row that will *become* part of a band after the first shift
            // e.g. 0 => fertility band starts off the map while 1 => fertility band starts with 1 row on the map
            int offset = shifts - row - 1 + gameConfig.F_BAND_INIT_POSITION;
            if (offset < 0){
                // Below fertility band
                newType = TileType.SOIL;
            }
            else if (offset < gameConfig.F_BAND_OUTER_HEIGHT){
                // Within first outer band
                newType = TileType.F_BAND_OUTER;
            }
            else if (offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT){
                // Within first mid band
                newType = TileType.F_BAND_MID;
            }
            else if (offset < gameConfig.F_BAND_OUTER_HEIGHT + gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within inner band
                newType = TileType.F_BAND_INNER;
            }
            else if (offset < gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within second mid band
                newType = TileType.F_BAND_MID;
            }
            else if (offset < 2 * gameConfig.F_BAND_OUTER_HEIGHT + 2 * gameConfig.F_BAND_MID_HEIGHT +
                    gameConfig.F_BAND_INNER_HEIGHT){
                // Within second outer band
                newType = TileType.F_BAND_OUTER;
            }
            else {
                // Above fertility bands
                newType = TileType.ARID;
            }

            // Only soil-type tiles can be affected
            tiles.get(row).forEach(tile -> {
                if (tile.getType() == TileType.ARID ||
                        tile.getType() == TileType.SOIL ||
                        tile.getType() == TileType.F_BAND_OUTER ||
                        tile.getType() == TileType.F_BAND_MID ||
                        tile.getType() == TileType.F_BAND_INNER){
                    tile.setType(newType);
                }
            });
        }
    }

    /** Grows all crops on this TileMap */
    public void growCrops(){
        for (Tile tile : this) {
            Crop crop = tile.getCrop();

            // Only affect crops which are still growing
            if (crop != null && crop.getType() != CropType.NONE && crop.getGrowthTimer() > 0) {
                // Increase value
                crop.grow(tile.getFertility());
                
                if (tile.isRainTotemEffect()) {
                    for (int i = 0; i < gameConfig.RAIN_TOTEM_GROWTH_MULTIPLIER - 1; i++) {
                        crop.grow(tile.getFertility());
                    }
                }
            }
            // Update tile states
            tile.setFertilityIdolEffect(false);
            tile.setRainTotemEffect(false);
        }
    }

    public boolean plantCrop(Position pos, CropType type, Player planter) {
        if (isValidPosition(pos) && isPlantable(pos)) {
            get(pos).setCrop(new Crop(type, gameConfig));
            get(pos).setPlanter(planter);
            return true;
        }
        return false;
    }

    private boolean isPlantable(Position pos) {
        for (TileType type : UNPLANTABLE_TILETYPES) {
            if (get(pos).getType() == type) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Position> getGreenGrocer() {
        return this.greenGrocerTiles;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public Tile get(Position pos) {
        return get(pos.getX(), pos.getY());
    }

    public Tile get(int x, int y) {
        if (!isValidPosition(x, y)) {
            return null;
        }

        return tiles.get(y).get(x);
    }

    public boolean isValidPosition(Position pos) {
        return isValidPosition(pos.getX(), pos.getY());
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }

    public TileType getTileType(Position pos) {
        return get(pos).getType();
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


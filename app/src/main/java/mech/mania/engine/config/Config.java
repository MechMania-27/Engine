package mech.mania.engine.config;

public class Config {
    // TODO: Determine naming convention
    private final int BOARD_HEIGHT;
    private final int BOARD_WIDTH;
    private final int F_BAND_MOVE_DELAY;
    private final int F_BAND_INNER_FERTILITY;
    private final int F_BAND_INNER_HEIGHT;
    private final int F_BAND_MID_FERTILITY;
    private final int F_BAND_MID_HEIGHT;
    private final int F_BAND_OUTER_FERTILITY;
    private final int F_BAND_OUTER_HEIGHT;
    private final int STARTING_SEED_QUALITY;
    private final int STARTING_MONEY;
    private final int MAX_MOVEMENT;
    private final int PLANT_RADIUS;
    private final int HARVEST_RADIUS;
    private final int CARRYING_CAPACITY;

    public int getBOARD_HEIGHT() {
        return BOARD_HEIGHT;
    }

    public int getBOARD_WIDTH() {
        return BOARD_WIDTH;
    }

    public int getF_BAND_MOVE_DELAY() {
        return F_BAND_MOVE_DELAY;
    }

    public int getF_BAND_INNER_FERTILITY() {
        return F_BAND_INNER_FERTILITY;
    }

    public int getF_BAND_INNER_HEIGHT() {
        return F_BAND_INNER_HEIGHT;
    }

    public int getF_BAND_MID_FERTILITY() {
        return F_BAND_MID_FERTILITY;
    }

    public int getF_BAND_MID_HEIGHT() {
        return F_BAND_MID_HEIGHT;
    }

    public int getF_BAND_OUTER_FERTILITY() {
        return F_BAND_OUTER_FERTILITY;
    }

    public int getF_BAND_OUTER_HEIGHT() {
        return F_BAND_OUTER_HEIGHT;
    }

    public int getSTARTING_SEED_QUALITY() {
        return STARTING_SEED_QUALITY;
    }

    public int getSTARTING_MONEY() {
        return STARTING_MONEY;
    }

    public int getMAX_MOVEMENT() {
        return MAX_MOVEMENT;
    }

    public int getPLANT_RADIUS() {
        return PLANT_RADIUS;
    }

    public int getHARVEST_RADIUS() {
        return HARVEST_RADIUS;
    }

    public int getCARRYING_CAPACITY() {
        return CARRYING_CAPACITY;
    }

    public Config(int board_height, int board_width, int f_band_move_delay, int f_band_inner_fertility, int f_band_inner_height, int f_band_mid_fertility, int f_band_mid_height, int f_band_outer_fertility, int f_band_outer_height, int starting_seed_quality, int starting_money, int max_movement, int plant_radius, int harvest_radius, int carrying_capacity) {
        BOARD_HEIGHT = board_height;
        BOARD_WIDTH = board_width;
        F_BAND_MOVE_DELAY = f_band_move_delay;
        F_BAND_INNER_FERTILITY = f_band_inner_fertility;
        F_BAND_INNER_HEIGHT = f_band_inner_height;
        F_BAND_MID_FERTILITY = f_band_mid_fertility;
        F_BAND_MID_HEIGHT = f_band_mid_height;
        F_BAND_OUTER_FERTILITY = f_band_outer_fertility;
        F_BAND_OUTER_HEIGHT = f_band_outer_height;
        STARTING_SEED_QUALITY = starting_seed_quality;
        STARTING_MONEY = starting_money;
        MAX_MOVEMENT = max_movement;
        PLANT_RADIUS = plant_radius;
        HARVEST_RADIUS = harvest_radius;
        CARRYING_CAPACITY = carrying_capacity;
    }

    public static Config getConfig() {
        String configFileName = System.getenv("CONFIG_FILENAME");

        // read file

        // create new config object
        // TODO: waiting on game properties file to get the proper deserialization format
        return new Config();
    }
}

package mech.mania.engine.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Config {
    // =========== BOARD CONSTANTS ===============
    public final int BOARD_HEIGHT;
    public final int BOARD_WIDTH;

    public final int F_BAND_MOVE_DELAY;
    public final double F_BAND_INNER_FERTILITY;
    public final int F_BAND_INNER_HEIGHT;
    public final double F_BAND_MID_FERTILITY;
    public final int F_BAND_MID_HEIGHT;
    public final double F_BAND_OUTER_FERTILITY;
    public final int F_BAND_OUTER_HEIGHT;

    // =========== GAME CONSTANTS ===============
    public final int STARTING_MONEY;
    public final int MAX_MOVEMENT;
    public final int PLANT_RADIUS;
    public final int HARVEST_RADIUS;
    public final int CARRYING_CAPACITY;
    public final int PROTECTION_RADIUS;

    // ========== OTHER CONSTANTS ===============
    public final String REPLAY_FILENAME;

    private ResourceBundle rb;

    public Config() throws MissingResourceException {
        this("mm27");
    }

    public Config(String resourceName) throws MissingResourceException {
        rb = ResourceBundle.getBundle(resourceName);

        // board props
        BOARD_HEIGHT =           Integer.parseInt(rb.getString("board.height"));
        BOARD_WIDTH =            Integer.parseInt(rb.getString("board.width"));
        F_BAND_INNER_HEIGHT =    Integer.parseInt(rb.getString("fertilityband.inner.height"));
        F_BAND_INNER_FERTILITY = Double.parseDouble(rb.getString("fertilityband.inner.fertility"));
        F_BAND_MID_HEIGHT =      Integer.parseInt(rb.getString("fertilityband.mid.height"));
        F_BAND_MID_FERTILITY =   Double.parseDouble(rb.getString("fertilityband.mid.fertility"));
        F_BAND_OUTER_HEIGHT =    Integer.parseInt(rb.getString("fertilityband.outer.height"));
        F_BAND_OUTER_FERTILITY = Double.parseDouble(rb.getString("fertilityband.outer.fertility"));
        F_BAND_MOVE_DELAY =      Integer.parseInt(rb.getString("fertilityband.speed"));

        // player props
        CARRYING_CAPACITY =      Integer.parseInt(rb.getString("player.carrycapacity"));
        MAX_MOVEMENT =           Integer.parseInt(rb.getString("player.maxmovement"));
        PLANT_RADIUS =           Integer.parseInt(rb.getString("player.plantradius"));
        HARVEST_RADIUS =         Integer.parseInt(rb.getString("player.harvestradius"));
        PROTECTION_RADIUS =      Integer.parseInt(rb.getString("player.protectionradius"));
        STARTING_MONEY =         Integer.parseInt(rb.getString("player.startingmoney"));

        // other props
        REPLAY_FILENAME = rb.getString("replayfile.name");
    }

}
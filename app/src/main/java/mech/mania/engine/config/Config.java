package mech.mania.engine.config;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.NoSuchElementException;

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
    public final int STARTING_SEED_QUALITY;
    public final int STARTING_MONEY;
    public final int MAX_MOVEMENT;
    public final int PLANT_RADIUS;
    public final int HARVEST_RADIUS;
    public final int CARRYING_CAPACITY;
    public final int PROTECTION_RADIUS;

    // ========== OTHER CONSTANTS ===============
    public final String REPLAY_FILENAME;

    public Config() throws ConfigurationException {
        this("mm27.xml");
    }

    public Config(String configFileName) throws ConfigurationException {
        // read file
        Configurations configurations = new Configurations();

        XMLConfiguration configRead = configurations.xml(configFileName);

        try {
            // board props
            BOARD_HEIGHT = configRead.getInt("appSettings.board.add(0)[@value]");
            BOARD_WIDTH = configRead.getInt("appSettings.board.add(1)[@value]");
            F_BAND_INNER_HEIGHT = configRead.getInt("appSettings.board.add(2)[@value]");
            F_BAND_INNER_FERTILITY = configRead.getDouble("appSettings.board.add(3)[@value]");
            F_BAND_MID_HEIGHT = configRead.getInt("appSettings.board.add(4)[@value]");
            F_BAND_MID_FERTILITY = configRead.getDouble("appSettings.board.add(5)[@value]");
            F_BAND_OUTER_HEIGHT = configRead.getInt("appSettings.board.add(6)[@value]");
            F_BAND_OUTER_FERTILITY = configRead.getDouble("appSettings.board.add(7)[@value]");
            F_BAND_MOVE_DELAY = configRead.getInt("appSettings.board.add(8)[@value]");

            // player props
            CARRYING_CAPACITY = configRead.getInt("appSettings.player.add(0)[@value]");
            MAX_MOVEMENT = configRead.getInt("appSettings.player.add(1)[@value]");
            PLANT_RADIUS = configRead.getInt("appSettings.player.add(2)[@value]");
            HARVEST_RADIUS = configRead.getInt("appSettings.player.add(3)[@value]");
            PROTECTION_RADIUS = configRead.getInt("appSettings.player.add(4)[@value]");
            STARTING_SEED_QUALITY = 0;  // configRead.getInt("");
            STARTING_MONEY = 0;  // configRead.getInt("");

            // crops props

            // other props
            REPLAY_FILENAME = "game.log";

        } catch (NoSuchElementException e) {
            throw new ConfigurationException("NoSuchElementException: " + e.getMessage());
        }
    }

}

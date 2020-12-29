package mech.mania.engine.config;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.util.NoSuchElementException;

public class Config {
    // =========== GAME CONSTANTS ===============
    private int boardHeight;
    private int boardWidth;
    private int fBandMoveDelay;
    private double fBandInnerFertility;
    private int fBandInnerHeight;
    private double fBandMidFertility;
    private int fBandMidHeight;
    private double fBandOuterFertility;
    private int fBandOuterHeight;

    private int startingSeedQuality;
    private int startingMoney;
    private int maxMovement;
    private int plantRadius;
    private int harvestRadius;
    private int carryingCapacity;
    private int protectionRadius;
    private String defaultReplayFileName;

    // ========== OTHER CONSTANTS ===============

    public int getBoardHeight() { return boardHeight; }

    public int getBoardWidth() { return boardWidth; }

    public int getfBandMoveDelay() { return fBandMoveDelay; }

    public double getfBandInnerFertility() { return fBandInnerFertility; }

    public int getfBandInnerHeight() { return fBandInnerHeight; }

    public double getfBandMidFertility() { return fBandMidFertility; }

    public int getfBandMidHeight() { return fBandMidHeight; }

    public double getfBandOuterFertility() { return fBandOuterFertility; }

    public int getfBandOuterHeight() { return fBandOuterHeight; }

    public int getStartingSeedQuality() { return startingSeedQuality; }

    public int getStartingMoney() { return startingMoney; }

    public int getMaxMovement() { return maxMovement; }

    public int getPlantRadius() { return plantRadius; }

    public int getHarvestRadius() { return harvestRadius; }

    public int getCarryingCapacity() { return carryingCapacity; }

    public int getProtectionRadius() { return protectionRadius; }

    public String getDefaultReplayFileName() { return defaultReplayFileName; }

    public Config() throws IOException, ConfigurationException {
        // String configFileName = System.getenv("MM27_CONFIG_FILENAME");
        // if (configFileName == null) {
        //     throw(new IOException("Environment variable MM27_CONFIG_FILENAME not set. Please set this to the name of the config file."));
        // }
        String configFileName = "mm27.xml";

        // read file
        Configurations configurations = new Configurations();

        XMLConfiguration configRead = configurations.xml(configFileName);


        try {
            // board props
            boardHeight = configRead.getInt("appSettings.board.add(0)[@value]");
            boardWidth = configRead.getInt("appSettings.board.add(1)[@value]");
            fBandInnerHeight = configRead.getInt("appSettings.board.add(2)[@value]");
            fBandInnerFertility = configRead.getDouble("appSettings.board.add(3)[@value]");
            fBandMidHeight = configRead.getInt("appSettings.board.add(4)[@value]");
            fBandMidFertility = configRead.getDouble("appSettings.board.add(5)[@value]");
            fBandOuterHeight = configRead.getInt("appSettings.board.add(6)[@value]");
            fBandOuterFertility = configRead.getDouble("appSettings.board.add(7)[@value]");
            fBandMoveDelay = configRead.getInt("appSettings.board.add(8)[@value]");

            // player props
            carryingCapacity = configRead.getInt("appSettings.player.add(0)[@value]");
            maxMovement = configRead.getInt("appSettings.player.add(1)[@value]");
            plantRadius = configRead.getInt("appSettings.player.add(2)[@value]");
            harvestRadius = configRead.getInt("appSettings.player.add(3)[@value]");
            protectionRadius = configRead.getInt("appSettings.player.add(4)[@value]");
            startingSeedQuality = 0;  // configRead.getInt("");
            startingMoney = 0;  // configRead.getInt("");

            // crops props

            // other props
            defaultReplayFileName = "game.log";

        } catch (NoSuchElementException e) {
            throw new ConfigurationException("NoSuchElementException: " + e.getMessage());
        }
    }

}

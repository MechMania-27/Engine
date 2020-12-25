package mech.mania.engine.config;

public class Config {
    // =========== GAME CONSTANTS ===============
    private int boardHeight;
    private int boardWidth;
    private int fBandMoveDelay;
    private int fBandInnerFertility;
    private int fBandInnerHeight;
    private int fBandMidFertility;
    private int fBandMidHeight;
    private int fBandOuterFertility;
    private int fBandOuterHeight;
    private int startingSeedQuality;
    private int startingMoney;
    private int maxMovement;
    private int plantRadius;
    private int harvestRadius;
    private int carryingCapacity;

    // ========== OTHER CONSTANTS ===============
    private int replayFileName;

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getfBandMoveDelay() {
        return fBandMoveDelay;
    }

    public int getfBandInnerFertility() {
        return fBandInnerFertility;
    }

    public int getfBandInnerHeight() {
        return fBandInnerHeight;
    }

    public int getfBandMidFertility() {
        return fBandMidFertility;
    }

    public int getfBandMidHeight() {
        return fBandMidHeight;
    }

    public int getfBandOuterFertility() {
        return fBandOuterFertility;
    }

    public int getfBandOuterHeight() {
        return fBandOuterHeight;
    }

    public int getStartingSeedQuality() {
        return startingSeedQuality;
    }

    public int getStartingMoney() {
        return startingMoney;
    }

    public int getMaxMovement() {
        return maxMovement;
    }

    public int getPlantRadius() {
        return plantRadius;
    }

    public int getHarvestRadius() {
        return harvestRadius;
    }

    public int getCarryingCapacity() {
        return carryingCapacity;
    }

    public String getDefaultReplayFileName() {
        return null;
    }

    public Config() {
        String configFileName = System.getenv("CONFIG_FILENAME");

        // read file

        // TODO: waiting on game properties file to get the proper deserialization format
    }
}

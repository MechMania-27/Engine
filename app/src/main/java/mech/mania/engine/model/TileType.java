package mech.mania.engine.model;

import java.util.ResourceBundle;

public enum TileType {
    GREEN_GROCER("tiletype.greengrocer"),
    GRASS("tiletype.grass"),  // unused until Visualizer implements
    ARID("tiletype.arid"),
    SOIL("tiletype.soil"),
    F_BAND_OUTER("tiletype.fband_outer"),
    F_BAND_MID("tiletype.fband_mid"),
    F_BAND_INNER("tiletype.fband_inner");

    /**
     * ResourceBundle to get properties file values from
     * Note: Since "mm27" is defined here, we cannot change the values for
     * the crop parameters by using a separate .properties file, since this
     * is initialized statically.
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle("mm27");

    /**
     * A prefix to use for getting future properties from the properties file
     * (via ResourceBundle). For example, the UpgradeType BIGGER_MUSCLES will
     * have all of its values taken from the upgradetype.biggermuscles.something
     * properties.
     */
    private String propsPrefix;
    TileType(String propsPrefix) {
        this.propsPrefix = propsPrefix;
    }

    public double getFertility() {
        return Double.parseDouble(rb.getString(propsPrefix + ".fertility"));
    }
}

package mech.mania.engine.model;

import java.util.ResourceBundle;

public enum CropType {
    NONE("croptype.none"),
    GRAPE("croptype.grape"),
    CORN("croptype.corn"),
    POTATO("croptype.potato"),
    JOGAN_FRUIT("croptype.jogan_fruit"),
    PEANUT("croptype.peanut"),
    QUADROTRITICALE("croptype.quadrotriticale"),
    DUCHAM_FRUIT("croptype.ducham_fruit"),
    GOLDEN_CORN("croptype.golden_corn");

    /**
     * ResourceBundle to get properties file values from
     * Note: Since "mm27" is defined here, we cannot change the values for
     * the crop parameters by using a separate .properties file, since this
     * is initialized statically.
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle("mm27");

    /**
     * A prefix to use for getting future properties from the properties file
     * (via ResourceBundle). For example, the CropType POTATO will
     * have all of its values taken from the croptype.potato.something
     * properties.
     */
    private final String propsPrefix;
    CropType(String propsPrefix) {
        this.propsPrefix = propsPrefix;
    }

//    public String getDescription() {
//        return rb.getString(propsPrefix + ".description");
//    }

    public static CropType getEnum(String crop) {
        if (crop == null || crop.length() == 0) {
            return CropType.NONE;
        }
        crop = crop.toUpperCase();
        crop = crop.replaceAll("-", "_");

        return CropType.valueOf(crop);
    }

    public double getFertilitySensitivity() {
        return Double.parseDouble(rb.getString(propsPrefix + ".fertilitysens"));
    }

    public int getGrowthTime() {
        return Integer.parseInt(rb.getString(propsPrefix + ".growthtime"));
    }

    public int getSeedBuyPrice() {
        return Integer.parseInt(rb.getString(propsPrefix + ".seedprice"));
    }

    public double getGrowthValuePerTurn() {
        return Double.parseDouble(rb.getString(propsPrefix + ".growthvalue"));
    }
}

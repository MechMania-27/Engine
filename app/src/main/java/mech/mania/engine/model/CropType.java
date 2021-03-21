package mech.mania.engine.model;

import java.util.ResourceBundle;

public enum CropType {
    NONE("croptype.none"),
    GRAPE("croptype.grape"),
    CORN("croptype.corn"),
    POTATO("croptype.potato");
    JORGANFRUIT("croptype.jorganfruit");
    PEANUTS("croptype.peanuts");
    QUADROTRITICALE("croptype.quadrotriticale");
    DUCHAMFRUIT("croptype.duchamfruit");
    GOLDENCORN("croptype.goldencorn");


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

    public String getDescription() {
        return rb.getString(propsPrefix + ".description");
    }

    public static CropType getEnum(String crop) {
        if (crop == null || crop.length() == 0) {
            return CropType.NONE;
        }
        crop = crop.toUpperCase();
        crop = crop.replaceAll("-", "_");

        return CropType.valueOf(crop);
    }

    public int getTimeToGrow() {
        return Integer.parseInt(rb.getString(propsPrefix + ".growthtime"));
    }

    public int getSeedBuyPrice() {
        return Integer.parseInt(rb.getString(propsPrefix + ".seedprice"));
    }

    public int getValueGrowth() {
        return Integer.parseInt(rb.getString(propsPrefix + ".valuegrowth"));
    }

}

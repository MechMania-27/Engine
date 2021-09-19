package mech.mania.engine.model;


import java.util.ResourceBundle;

public enum ItemType {
    RAIN_TOTEM,
    FERTILITY_IDOL,
    PESTICIDE,
    SCARECROW,
    DELIVERY_DRONE,
    COFFEE_THERMOS,
    NONE;

    /**
     * ResourceBundle to get properties file values from
     * Note: Since "mm27" is defined here, we cannot change the values for
     * the crop parameters by using a separate .properties file, since this
     * is initialized statically.
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle("mm27");

    public static ItemType getEnum(String item) {
        if (item == null || item.length() == 0) {
            return ItemType.NONE;
        }
        item = item.toUpperCase();
        item = item.replaceAll("-", "_");

        // handle any two word items
        switch (item) {
            case "FERTILITYIDOL":
                return ItemType.FERTILITY_IDOL;
            case "RAINTOTEM":
                return ItemType.RAIN_TOTEM;
            case "DELIVERYDRONE":
                return ItemType.DELIVERY_DRONE;
            case "COFFEETHERMOS":
                return ItemType.COFFEE_THERMOS;
        }

        return ItemType.valueOf(item);
    }
}

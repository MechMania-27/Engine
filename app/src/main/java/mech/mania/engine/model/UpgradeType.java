package mech.mania.engine.model;

import java.util.ResourceBundle;

public enum UpgradeType {
    LONGER_SCYTHE("upgradetype.longerscythe"),
    LOYALTY_CARD("upgradetype.loyaltycard"),
    LONGER_LEGS("upgradetype.longerlegs"),
    RABBITS_FOOT("upgradetype.rabbitsfoot"),
    SEED_A_PULT("upgradetype.seedapult"),
    SPYGLASS("upgradetype.spyglass"),
    BACKPACK("upgradetype.backpack"),
    NONE("upgradetype.none");

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
    UpgradeType(String propsPrefix) {
        this.propsPrefix = propsPrefix;
    }

    public static UpgradeType getEnum(String upgrade) {
        if (upgrade == null || upgrade.length() == 0) {
            return UpgradeType.NONE;
        }
        upgrade = upgrade.toUpperCase();
        upgrade = upgrade.replaceAll("[-_]", "");

        // handle any two word upgrades
        switch (upgrade) {
            case "SEEDAPULT":
                return UpgradeType.SEED_A_PULT;
            case "SPYGLASS":
                return UpgradeType.SPYGLASS;
            case "LOYALTYCARD":
                return UpgradeType.LOYALTY_CARD;
            case "LONGERLEGS":
                return UpgradeType.LONGER_LEGS;
            case "LONGERSCYTHE":
                return UpgradeType.LONGER_SCYTHE;
            case "RABBITSFOOT":
                return UpgradeType.RABBITS_FOOT;
            case "BACKPACK":
                return UpgradeType.BACKPACK;
            default:
                return UpgradeType.NONE;
        }
    }
}

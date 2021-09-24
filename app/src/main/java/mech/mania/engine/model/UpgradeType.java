package mech.mania.engine.model;

public enum UpgradeType {
    SCYTHE,
    LOYALTY_CARD,
    LONGER_LEGS,
    RABBITS_FOOT,
    SEED_A_PULT,
    SPYGLASS,
    BACKPACK,
    NONE;

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
                return UpgradeType.SCYTHE;
            case "RABBITSFOOT":
                return UpgradeType.RABBITS_FOOT;
            case "BACKPACK":
                return UpgradeType.BACKPACK;
            default:
                return UpgradeType.NONE;
        }
    }
}

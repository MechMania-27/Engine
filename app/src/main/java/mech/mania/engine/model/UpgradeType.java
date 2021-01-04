package mech.mania.engine.model;

public enum UpgradeType {
    BIGGER_MUSCLES,
    LONGER_SCYTHE,
    LONGER_LEGS,
    RABBITS_FOOT,
    NONE;

    public static UpgradeType getEnum(String upgrade) {
        if (upgrade == null || upgrade.length() == 0) {
            return UpgradeType.NONE;
        }
        upgrade = upgrade.toUpperCase();
        upgrade = upgrade.replaceAll("-", "_");

        // handle any two word upgrades
        switch (upgrade) {
            case "BIGGERMUSCLES":
                return UpgradeType.BIGGER_MUSCLES;
            case "LONGERLEGS":
                return UpgradeType.LONGER_LEGS;
            case "LONGERSCYTHE":
                return UpgradeType.LONGER_SCYTHE;
            case "RABBITSFOOT":
                return UpgradeType.RABBITS_FOOT;
        }

        return UpgradeType.valueOf(upgrade);
    }
}

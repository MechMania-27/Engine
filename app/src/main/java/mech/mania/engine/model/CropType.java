package mech.mania.engine.model;

public enum CropType {
    POTATO(10, 2, 5),
    CORN(15, 3, 5),
    DUCHAMFRUIT(25, 25, 2),
    NONE(0, 0, 0);

    private int timeToGrow;
    private int seedBuyPrice;
    private int grownPlantSellPrice;
    CropType(int timeToGrow, int seedBuyPrice, int grownPlantSellPrice) {
        this.timeToGrow = timeToGrow;
        this.seedBuyPrice = seedBuyPrice;
        this.grownPlantSellPrice = grownPlantSellPrice;
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
        return timeToGrow;
    }

    public int getSeedBuyPrice() {
        return seedBuyPrice;
    }

    public int getGrownPlantSellPrice() {
        return grownPlantSellPrice;
    }
}

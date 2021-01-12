package mech.mania.engine.model;

public class Crop {
    private CropType type;
    private int growthTimer;

    public Crop(CropType type) {
        this.type = type;
        this.growthTimer = 0;
    }

    public int getGrowthTimer() {
        return growthTimer;
    }

    public void setGrowthTimer(int growthTimer) {
        this.growthTimer = growthTimer;
    }

    public CropType getType() {
        return type;
    }

    public void setType(CropType type) {
        this.type = type;
    }

}

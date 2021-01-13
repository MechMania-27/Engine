package mech.mania.engine.model;

public class Crop {
    private CropType type;
    private int growthTimer; // This is a down-counter (i.e. 0 means fully grown)
    private double value;

    public Crop(CropType type) {
        this.type = type;
        this.growthTimer = 0;
        this.value = 0;
    }

    /** Increases this crop's value based on the multiplier and decrements the growthTimer if crop is still growing */
    public void grow(double multiplier){
        if(growthTimer > 0) {
            value += type.getValueGrowth() * multiplier;
            growthTimer--;
        }
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

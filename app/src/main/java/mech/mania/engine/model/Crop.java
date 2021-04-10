package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Crop {
    @Expose
    private CropType type;
    @Expose
    private int growthTimer; // This is a down-counter (i.e. 0 means fully grown)
    @Expose
    private double value;

    public Crop(CropType type) {
        this.type = type;
        this.growthTimer = type.getTimeToGrow();
        this.value = 0;
    }

    public Crop(Crop other) {
        this.type = other.type;
        this.growthTimer = other.growthTimer;
        this.value = other.value;
    }

    /** Increases this crop's value based on the multiplier and decrements the growthTimer if crop is still growing */
    public void grow(double multiplier){
        if(growthTimer > 0) {
            value += type.getValueGrowth() * multiplier;
            growthTimer--;
        }
    }

    public double getValue() {
        return value;
    }

    public int getGrowthTimer() {
        return growthTimer;
    }

    public CropType getType() {
        return type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void applyPesticide() {
        this.value *= 0.8;
    }

    public void setGrowthTimer(int growthTimer) {
        this.growthTimer = growthTimer;
    }
}

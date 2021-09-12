package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

public class Crop {
    @Expose
    private CropType type;
    @Expose
    private int growthTimer; // This is a down-counter (i.e. 0 means fully grown)
    @Expose
    private double value;

    private final Config gameConfig;

    public Crop(CropType type, Config gameConfig) {
        this.type = type;
        this.growthTimer = type.getTimeToGrow();
        this.value = 0;
        this.gameConfig = gameConfig;
    }

    public Crop(Crop other) {
        this.type = other.type;
        this.growthTimer = other.growthTimer;
        this.value = other.value;
        this.gameConfig = other.gameConfig;
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
        this.value *= (1 - gameConfig.PESTICIDE_CROP_VALUE_DECREASE);
    }

    public void setGrowthTimer(int growthTimer) {
        this.growthTimer = growthTimer;
    }

    public String toString() {
        return String.format("[%s,%d,%.2f]", type, growthTimer, value);
    }
}

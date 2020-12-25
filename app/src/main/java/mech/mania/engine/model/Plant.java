package mech.mania.engine.model;

public class Plant {
    private PlantType type;
    private int growthTimer;

    public Plant(PlantType type, int growthTimer) {
        this.type = type;
        this.growthTimer = growthTimer;
    }

    public int getGrowthTimer() {
        return growthTimer;
    }

    public void setGrowthTimer(int growthTimer) {
        this.growthTimer = growthTimer;
    }

    public PlantType getType() {
        return type;
    }

    public void setType(PlantType type) {
        this.type = type;
    }

}

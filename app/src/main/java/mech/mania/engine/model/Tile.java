package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Tile {
    @Expose
    private TileType type;
    @Expose
    private double soilQuality;
    @Expose
    private Crop crop;
    @Expose
    private ItemType item;

    private int turnsLeftToGrow;
    private Player planter;

    private boolean rainTotemEffect = false;
    private boolean fertilityIdolEffect = false;
    private boolean pesticideEffect = false;
    private boolean scarecrowEffect = false;

    public Tile(TileType type, int soilQuality) {
        this.type = type;
        this.soilQuality = soilQuality;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public double getSoilQuality() {
        return soilQuality;
    }

    public void setSoilQuality(int soilQuality) {
        this.soilQuality = soilQuality;
    }

    public boolean isRainTotemEffect() {
        return rainTotemEffect;
    }

    public void setRainTotemEffect(boolean rainTotemEffect) {
        this.rainTotemEffect = rainTotemEffect;
    }

    public boolean isFertilityIdolEffect() {
        return fertilityIdolEffect;
    }

    public void setFertilityIdolEffect(boolean fertilityIdolEffect) {
        this.fertilityIdolEffect = fertilityIdolEffect;
    }

    public boolean isPesticideEffect() {
        return pesticideEffect;
    }

    public void setPesticideEffect(boolean pesticideEffect) {
        this.pesticideEffect = pesticideEffect;
    }

    public boolean isScarecrowEffect() {
        return scarecrowEffect;
    }

    public void setScarecrowEffect(boolean scarecrowEffect) {
        this.scarecrowEffect = scarecrowEffect;
    }
}

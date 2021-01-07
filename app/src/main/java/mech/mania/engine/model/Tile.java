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
    private ItemType p1Item;
    @Expose
    private ItemType p2Item;

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

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public ItemType getP1Item() {
        return p1Item;
    }

    public void setP1Item(ItemType p1Item) {
        this.p1Item = p1Item;
    }

    public ItemType getP2Item() {
        return p2Item;
    }

    public void setP2Item(ItemType p2Item) {
        this.p2Item = p2Item;
    }

}

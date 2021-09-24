package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mech.mania.engine.config.Config;

import java.util.Objects;

public class Tile {
    @Expose
    private TileType type;
    @Expose
    private Crop crop;
    @Expose
    @SerializedName("p1_item")
    private ItemType p1Item;
    @Expose
    @SerializedName("p2_item")
    private ItemType p2Item;

    private static Config gameConfig;
  
    @Expose
    private int turnsLeftToGrow;
    @Expose
    private Player planter;

    @Expose
    private boolean rainTotemEffect = false;
    @Expose
    private boolean fertilityIdolEffect = false;
    @Expose
    private int scarecrowEffect = -1;

    public Tile(TileType type, Config gameConfig) {
        this.type = type;
        this.crop = new Crop(CropType.NONE, gameConfig);
        this.p1Item = ItemType.NONE;
        this.p2Item = ItemType.NONE;
        Tile.gameConfig = gameConfig;
    }

    public Tile(Tile other) {
        this.type = other.type;
        this.crop = new Crop(other.crop);
        this.p1Item = other.p1Item;
        this.p2Item = other.p2Item;
        this.planter = other.planter;
        this.turnsLeftToGrow = other.turnsLeftToGrow;
        this.rainTotemEffect = other.rainTotemEffect;
        this.fertilityIdolEffect = other.fertilityIdolEffect;
        this.scarecrowEffect = other.scarecrowEffect;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tile)) {
            return false;
        }
        
        Tile other = (Tile) obj;
        if (!type.equals(other.type)) return false;
        if (!crop.equals(other.crop)) return false;
        if (p1Item != other.p1Item) return false;
        if (p2Item != other.p2Item) return false;
        if (!Objects.equals(planter, other.planter)) return false;
        if (turnsLeftToGrow != other.turnsLeftToGrow) return false;
        if (rainTotemEffect != other.rainTotemEffect) return false;
        if (fertilityIdolEffect != other.fertilityIdolEffect) return false;
        if (scarecrowEffect != other.scarecrowEffect) return false;

        return true;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
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

    public int isScarecrowEffect() {
        return scarecrowEffect;
    }

    public void setScarecrowEffect(int scarecrowEffect) {
        this.scarecrowEffect = scarecrowEffect;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public void clearCrop() {
        this.crop = new Crop(CropType.NONE, gameConfig);
    }

    // these two methods are unnecessary because only the starter packs will use them
//    public ItemType getP1Item() {
//        return p1Item;
//    }

//    public ItemType getP2Item() {
//        return p2Item;
//    }

    public void setP1Item(ItemType p1Item) {
        this.p1Item = p1Item;
    }

    public void setP2Item(ItemType p2Item) {
        this.p2Item = p2Item;
    }

    public double getFertility() {
        if (isFertilityIdolEffect()) {
            return gameConfig.FERTILITY_IDOL_FERTILITY_MULTIPLIER * type.getFertility();
        }
        return type.getFertility();
    }
    public void setPlanter(Player p) {
        planter = p;
    }
    @Override
    public String toString() {
        return String.format("Tile[type=%s,crop=%s]", type, crop);
    }
    public Player getPlanter() {
        return planter;
    }

}

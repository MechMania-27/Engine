package mech.mania.engine.model;

public class TileState {
    private int soilQuality;
    private Plant plant;
    private Item item;

    public TileState(int soilQuality, Plant plant, Item item) {
        this.soilQuality = soilQuality;
        this.plant = plant;
        this.item = item;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public int getSoilQuality() {
        return soilQuality;
    }

    public void setSoilQuality(int soilQuality) {
        this.soilQuality = soilQuality;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

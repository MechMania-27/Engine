package mech.mania.engine.model;

public class Item {
    private ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}

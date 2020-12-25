package mech.mania.engine.model;

public class Item {
    private ItemType type;
    private String description;  // optional

    public Item(ItemType type) {
        this.type = type;
    }

    public Item(ItemType type, String description) {
        this.type = type;
        this.description = description;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

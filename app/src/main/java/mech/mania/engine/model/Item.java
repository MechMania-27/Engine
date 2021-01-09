package mech.mania.engine.model;

public enum Item {
    RAIN_TOTEM("Rain totem"),
    FERTILITY_IDOL("Fertility idol"),
    PESTICIDE("Pesticide"),
    SCARECROW("Scarecrow"),
    NONE("");

    String description;
    Item(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

/*

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
*/

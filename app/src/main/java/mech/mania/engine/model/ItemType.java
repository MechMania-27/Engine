package mech.mania.engine.model;

public enum ItemType {
    RAIN_TOTEM("Rain totem"),
    FERTILITY_IDOL("Fertility idol"),
    PESTICIDE("Pesticide"),
    SCARECROW("Scarecrow"),
    NONE("");

    String description;
    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ItemType getEnum(String item) {
        if (item == null || item.length() == 0) {
            return ItemType.NONE;
        }
        item = item.toUpperCase();
        item = item.replaceAll("-", "_");

        // handle any two word items
        switch (item) {
            case "FERTILITYIDOL":
                return ItemType.FERTILITY_IDOL;
            case "RAINTOTEM":
                return ItemType.RAIN_TOTEM;
        }

        return ItemType.valueOf(item);
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

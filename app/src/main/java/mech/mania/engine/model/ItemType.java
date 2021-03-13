package mech.mania.engine.model;


import java.util.ResourceBundle;
import java.util.function.BiFunction;

public enum ItemType {
    RAIN_TOTEM("itemtype.raintotem", ItemType::rainTotemProcess),
    FERTILITY_IDOL("itemtype.fertilityidol", ItemType::fertilityIdolProcess),
    PESTICIDE("itemtype.pesticide", ItemType::pesticideProcess),
    SCARECROW("itemtype.scarecrow", ItemType::scarecrowProcess),
    NONE("itemtype.none", ItemType::nothing);

    /**
     * ResourceBundle to get properties file values from
     * Note: Since "mm27" is defined here, we cannot change the values for
     * the crop parameters by using a separate .properties file, since this
     * is initialized statically.
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle("mm27");

    /**
     * A prefix to use for getting future properties from the properties file
     * (via ResourceBundle). For example, the ItemType RAIN_TOTEM will
     * have all of its values taken from the itemtype.raintotem.something
     * properties.
     */
    private String propsPrefix;
    BiFunction<Player, TileMap, Boolean> applyProcess;
    ItemType(String propsPrefix, BiFunction<Player, TileMap, Boolean> process) {
        this.propsPrefix = propsPrefix;
        this.applyProcess = process;
    }

    public BiFunction<Player, TileMap, Boolean> getApplyProcess() {
        return applyProcess;
    }

    public String getDescription() {
        return rb.getString(propsPrefix + ".description");
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

    private static boolean rainTotemProcess(Player player, TileMap tilemap){
        if (!tilemap.isValidPosition(player.getPosition())) return false;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j){
                Tile tile = tilemap.getTile(new Position(i - 2, j - 2));
                if (tile != null){
                    tile.setRainTotemEffect(true);
                }
            }
        }
        return true;
    }
    private static boolean fertilityIdolProcess(Player player, TileMap tilemap){
        if (!tilemap.isValidPosition(player.getPosition())) return false;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j){
                Tile tile = tilemap.getTile(new Position(i - 2, j - 2));
                if (tile != null){
                    tile.setFertilityIdolEffect(true);
                }
            }
        }
        return true;
    }
    private static boolean pesticideProcess(Player player, TileMap tilemap){
        if (!tilemap.isValidPosition(player.getPosition())) return false;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j){
                Tile tile = tilemap.getTile(new Position(i - 1, j - 1));
                if (tile != null){
                    tile.setPesticideEffect(true);
                }
            }
        }
        return true;
    }
    private static boolean scarecrowProcess(Player player, TileMap tilemap){
        if (!tilemap.isValidPosition(player.getPosition())) return false;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j){
                Tile tile = tilemap.getTile(new Position(i - 2, j - 2));
                if (tile != null){
                    tile.setScarecrowEffect(true);
                }
            }
        }
        return true;
    }

    private static boolean deliveryDroneProcess(Player player, TileMap tilemap) {
        player.setDeliveryDrone(true);
        return true;
    }

    private static boolean coffeeThermosProcess(Player player, TileMap tilemap) {
        player.setUseCoffeeThermos(true);
        return true;
    }

    private static boolean nothing(Player player, TileMap tilemap){
        // do nothing
        return true;
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

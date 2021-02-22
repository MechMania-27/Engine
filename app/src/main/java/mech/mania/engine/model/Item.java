package mech.mania.engine.model;

import java.util.function.BiFunction;

public class Item {
    ItemType itemType;
    public Item(ItemType it) {
        this.itemType = it;
    }
    public void useItem(Player player, TileMap tilemap){
        itemType.applyProcess.apply(player, tilemap);
    }

}

package mech.mania.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Item;
import mech.mania.engine.model.PlayerDecision;
import mech.mania.engine.model.Upgrade;

public class PlayerParseUtils {
    public static Item itemFromString(String item) {
        if (item == null || item.length() == 0) {
            return Item.NONE;
        }
        item = item.toUpperCase();
        item = item.replaceAll("[\\s-]", "_");

        // handle any two word items
        switch (item) {
            case "FERTILITYIDOL":
                return Item.FERTILITY_IDOL;
            case "RAINTOTEM":
                return Item.RAIN_TOTEM;
        }
        return Item.valueOf(item);
    }

    public static Upgrade upgradeFromString(String upgrade) {
        if (upgrade == null || upgrade.length() == 0) {
            return Upgrade.NONE;
        }
        upgrade = upgrade.toUpperCase();
        upgrade = upgrade.replaceAll("[\\s-]", "_");

        // handle any two word upgrades
        switch (upgrade) {
            case "BIGGERMUSCLES":
                return Upgrade.BIGGER_MUSCLES;
            case "LONGERLEGS":
                return Upgrade.LONGER_LEGS;
            case "LONGERSCYTHE":
                return Upgrade.LONGER_SCYTHE;
            case "RABBITSFOOT":
                return Upgrade.RABBITS_FOOT;
        }
        return Upgrade.valueOf(upgrade);
    }

    public static String sendInfoFromGameState(GameState gameState) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(gameState, GameState.class);
    }

    public static PlayerDecision decisionFromString(String decision) {
        // TODO: change this so that decisions can be made using human-readable commands
        // one line `move 1 1;plant crop1 2 2` vs two lines `move 1 1` then `plant crop1 2 2`?
        return null;
    }
}

package mech.mania.engine.util;

import com.google.gson.Gson;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Item;
import mech.mania.engine.model.PlayerDecision;
import mech.mania.engine.model.Upgrade;

public class PlayerParseUtils {
    public static Item itemFromString(String item) {
        return Item.valueOf(item);
    }

    public static Upgrade upgradeFromString(String upgrade) {
        return Upgrade.valueOf(upgrade);
    }

    public static String gameStateToString(GameState gameState) {
        return new Gson().toJson(gameState, GameState.class);
    }

    public static PlayerDecision decisionFromString(String decision) {
        // TODO: change this so that decisions can be made using human-readable commands
        return new Gson().fromJson(decision, PlayerDecision.class);
    }
}

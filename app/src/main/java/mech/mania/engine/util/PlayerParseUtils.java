package mech.mania.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.model.*;
import mech.mania.engine.model.decisions.*;

public class PlayerParseUtils {
    /**
     * Parse an item object from a string
     * @param item String to parse
     * @return Item enum corresponding to string
     */
    public static ItemType itemFromString(String item) {
        return ItemType.getEnum(item);
    }

    /**
     * Parse an upgrade object from a string
     * @param upgrade String to parse
     * @return Upgrade enum corresponding to string
     */
    public static UpgradeType upgradeFromString(String upgrade) {
        return UpgradeType.getEnum(upgrade);
    }

    /**
     * Parse a plant type from a string
     * TODO
     * @param plant String to parse
     * @return PlantType that was parsed
     */
    protected static CropType plantTypeFromString(String plant) {
        return CropType.getEnum(plant);
    }

    /**
     * Function that generates the String to be sent to the bot given the GameState
     * @param gameState GameState to use
     * @return String to give to bot
     */
    public static String sendInfoFromGameState(GameState gameState) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(gameState, GameState.class);
    }


    /**
     * Modify the decision object by parsing the decisionString and putting the necessary
     *
     * information inside the decision object
     * @param playerID ID of the player who made this decision
     * @param decisionString String to parse
     * @return PlayerDecision object
     */
    public static PlayerDecision parseDecision(String playerID, String decisionString) throws PlayerDecisionParseException {
        String[] tokens = decisionString.strip().split("\\s");
        switch (tokens[0].toLowerCase()) {
            case "move":
                return new MoveAction(playerID, decisionString);
            case "plant":
                return new PlantAction(playerID, decisionString);
            case "harvest":
                return new HarvestAction(playerID, decisionString);
            case "buy":
                return new BuyAction(playerID, decisionString);
            case "useitem": case "use_item":
                // TODO: Should we support "use item" as well?
                return new UseItemAction(playerID, decisionString);
            default:
                throw new PlayerDecisionParseException("Unrecognized action: " + tokens[0]);
        }
    }
}

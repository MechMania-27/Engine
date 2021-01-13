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
     * @param playerID ID of the player who made this decision (0 for player1, 1 for player2)
     * @param decisionString String to parse
     * @return PlayerDecision object
     */
    public static PlayerDecision parseDecision(int playerID, String decisionString) throws PlayerDecisionParseException {
        int space = decisionString.indexOf(' ');
        if (space == -1) {
            throw new PlayerDecisionParseException(String.format("Action type not found in string: %s", decisionString));
        }

        String action = decisionString.substring(0, space);
        // substring from space to end and strip all trailing whitespace
        String args = decisionString.substring(space + 1).replaceAll("\\s+$", "");

        switch (action.toLowerCase()) {
            case "move":
                return new MoveAction(playerID).parse(args);
            case "plant":
                return new PlantAction(playerID).parse(args);
            case "harvest":
                return new HarvestAction(playerID).parse(args);
            case "buy":
                return new BuyAction(playerID).parse(args);
            case "useitem": case "use_item":
                return new UseItemAction(playerID).parse(args);
            default:
                throw new PlayerDecisionParseException(String.format("Unrecognized action: %s", action));
        }
    }
}

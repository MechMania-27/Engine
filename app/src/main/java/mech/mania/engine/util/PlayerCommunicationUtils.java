package mech.mania.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.model.decisions.*;

import java.util.List;


public class PlayerCommunicationUtils {
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
     * @param playerNum player number to set for the game state (0 or 1)
     * @return String to give to bot
     */
    public static String sendInfoFromGameState(GameState gameState, int playerNum, List<String> feedback) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        gameState.setPlayerNum(playerNum + 1);
        gameState.setFeedback(feedback);
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
    public static PlayerDecision parseDecision(int playerID, String decisionString,
                                               JsonLogger playerLogger, JsonLogger engineLogger) throws PlayerDecisionParseException {
        int space = decisionString.indexOf(' ');
        if (space == -1) {
            String message = String.format("Action type not found in string: %s", decisionString);
            playerLogger.feedback(message);
            throw new PlayerDecisionParseException(message);
        }

        String action = decisionString.substring(0, space);
        // substring from space to end and strip all trailing whitespace
        String args = decisionString.substring(space + 1).replaceAll("\\s+$", "");

        switch (action.toLowerCase()) {
            case "move":
                return new MoveDecision(playerID, playerLogger, engineLogger).parse(args);
            case "plant":
                return new PlantDecision(playerID, playerLogger, engineLogger).parse(args);
            case "harvest":
                return new HarvestDecision(playerID, playerLogger, engineLogger).parse(args);
            case "buy":
                return new BuyDecision(playerID, playerLogger, engineLogger).parse(args);
            case "useitem": case "use_item":
                return new UseItemDecision(playerID, playerLogger, engineLogger).parse(args);
            case "donothing": case "do_nothing":
                return new DoNothingDecision(playerID, playerLogger, engineLogger).parse(args);
            default:
                String message = String.format("Unrecognized action: %s", action);
                playerLogger.feedback(message);
                throw new PlayerDecisionParseException(message);
        }
    }
}

package mech.mania.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.model.*;

import java.util.List;

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
     * Parse a decision from the list of decisionStrings that were sent from the bot
     * @param decisionStrings List of Strings that were given from the bot
     * @return a PlayerDecision object containing information about the actions made
     */
    public static PlayerDecision decisionFromString(List<String> decisionStrings) {
        PlayerDecision decision = new PlayerDecision();

        for (String decisionString : decisionStrings) {
            parseDecision(decision, decisionString);
        }

        return decision;
    }

    /**
     * Modify the decision object by parsing the decisionString and putting the necessary
     *
     * information inside the decision object
     * @param decision PlayerDecision object to add to
     * @param decisionString String to parse
     */
    protected static void parseDecision(PlayerDecision decision, String decisionString) {
        String[] tokens = decisionString.split("\\s");
        switch (tokens[0].toLowerCase()) {
            case "move":
                if (decision.getMovePos() != null) {
                    // a move decision was already made
                    break;
                }
                int moveX = Integer.parseInt(tokens[1]), moveY = Integer.parseInt(tokens[2]);
                decision.setMovePos(new Position(moveX, moveY));
                break;
            case "plant":
                if (decision.getAction() != null) {
                    // another action was already made, only take the first action
                    break;
                }
                decision.setAction(PlayerDecision.ActionType.PLANT);
                decision.addSeed(new Crop(plantTypeFromString(tokens[1])));
                int plantX = Integer.parseInt(tokens[2]), plantY = Integer.parseInt(tokens[3]);
                decision.addActionPosition(new Position(plantX, plantY));
                break;
            case "harvest":
                if (decision.getAction() != null) {
                    // another action was already made, only take the first action
                    break;
                }
                decision.setAction(PlayerDecision.ActionType.HARVEST);
                int harvestX = Integer.parseInt(tokens[1]), harvestY = Integer.parseInt(tokens[2]);
                decision.addActionPosition(new Position(harvestX, harvestY));
                break;
            case "buy":
                if (decision.getAction() != null) {
                    // another action was already made, only take the first action
                    break;
                }
                decision.setAction(PlayerDecision.ActionType.BUY);
                decision.addSeed(new Crop(plantTypeFromString(tokens[1])));
                decision.addBuyAmount(Integer.parseInt(tokens[2]));
                break;
            case "sell":
                if (decision.getAction() != null) {
                    // another action was already made, only take the first action
                    break;
                }
                decision.setAction(PlayerDecision.ActionType.SELL);
                break;
            case "use":
                if (tokens[1].equals("item")) {
                    tokens[0] = "useitem";
                    System.arraycopy(tokens, 2, tokens, 1, tokens.length - 2);
                } else {
                    // invalid command
                    break;
                }
            case "useitem": case "use_item":
                if (decision.getAction() != null) {
                    // another action was already made, only take the first action
                    break;
                }
                decision.setAction(PlayerDecision.ActionType.USE_ITEM);
                decision.setItemToUse(itemFromString(tokens[1]));
                int itemX = Integer.parseInt(tokens[2]), itemY = Integer.parseInt(tokens[3]);
                decision.addActionPosition(new Position(itemX, itemY));
                break;
        }
    }
}

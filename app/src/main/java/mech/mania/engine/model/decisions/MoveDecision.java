package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveDecision extends PlayerDecision {
    protected Position destination;

    public MoveDecision(int playerID, JsonLogger playerLogger, JsonLogger engineLogger) {
        super(playerLogger, engineLogger);
        this.playerID = playerID;
        this.destination = null;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        if (matcher.find()) {
            try {
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                destination = new Position(x, y);
            } catch (NumberFormatException e) {
                // will occur if input can't be parsed into an int (ex: Integer.MAX_VALUE + 1)
                String message = "Arguments did not match Move regex (did you pass too big an int?)";
                playerLogger.feedback(message);
                throw new PlayerDecisionParseException(message);
            }
        } else {
            String message = "Arguments did not match Move regex";
            playerLogger.feedback(message);
            throw new PlayerDecisionParseException(message);
        }

        return this;
    }

    public void performAction(GameState state) {
        if (this.destination == null) {
            String message = "Failed to move to null position";
            playerLogger.feedback(message);
            engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
        }

        Player player = state.getPlayer(playerID);

        if (!state.getTileMap().isValidPosition(this.destination)) {
            String message = String.format("Failed to move to position %s, invalid destination", destination);
            playerLogger.feedback(message);
            engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
            return;
        }
        if (GameUtils.distance(this.destination, player.getPosition()) > player.getSpeed()) {
            String message = String.format("Failed to move to position %s, greater than allowed movement (%d > %d)",
                    destination, GameUtils.distance(this.destination, player.getPosition()), player.getSpeed());
            playerLogger.feedback(message);
            engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
            return;
        }

        player.setPosition(this.destination);

        if (state.getTileMap().get(this.destination).getType() == TileType.GREEN_GROCER) {
            String message = "Selling inventory";
            playerLogger.feedback(message);
            engineLogger.info(String.format("Player %d: " + message, playerID + 1));
            player.sellInventory();
        }
    }
}

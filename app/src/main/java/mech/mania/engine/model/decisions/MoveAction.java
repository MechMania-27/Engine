package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveAction extends PlayerDecision {
    private Position destination;

    public MoveAction(int playerID) {
        this.playerID = playerID;
        this.destination = null;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            destination = new Position(x, y);
        } else{
            throw new PlayerDecisionParseException("Arguments did not match Move regex");
        }

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        if (this.destination == null) {
            engineLogger.severe(String.format("Failed to move player %d to null position", playerID + 1));
        }

        Player player = state.getPlayer(playerID);

        if (!state.getTileMap().isValidPosition(this.destination)) {
            engineLogger.severe(String.format("Player %d failed to move to position %s, invalid destination", playerID + 1, destination));
            return;
        }
        if (GameUtils.distance(this.destination, player.getPosition()) > player.getSpeed()) {
            engineLogger.severe(String.format("Player %d failed to move to position %s, greater than allowed movement", playerID + 1, destination));
            return;
        }

        player.setPosition(this.destination);

        if (state.getTileMap().get(this.destination).getType() == TileType.GREEN_GROCER) {
            player.sellInventory();
        }
    }
}

package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.Position;

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
        // note: destination can be null
        if (destination != null) {
            if (playerID == 0) {
                if (state.getTileMap().isValidPosition(destination)) {
                    state.getPlayer1().setPosition(destination);
                    if (state.getTileMap().isGreenGrocer(destination)) {
                        state.getPlayer1().sellInventory();
                    }
                } else {
                    engineLogger.severe(String.format("Failed to move player 1 to position %s", destination));
                }
            } else {
                if (state.getTileMap().isValidPosition(destination)) {
                    state.getPlayer2().setPosition(destination);
                    if (state.getTileMap().isGreenGrocer(destination)) {
                        state.getPlayer2().sellInventory();
                    }
                } else {
                    engineLogger.severe(String.format("Failed to move player 2 to position %s", destination));
                }
            }
        }
    }
}

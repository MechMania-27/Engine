package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HarvestAction extends PlayerDecision {
    private ArrayList<Position> coords;

    public HarvestAction(int playerID){
        this.playerID = playerID;
        this.coords = new ArrayList<>();
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        coords = new ArrayList<>();

        // Command must have at least one result
        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match Harvest regex");
        }

        do {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            coords.add(new Position(x, y));
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        // will use playerID to get the Player object from state and then validate each planting action
        Player player = state.getPlayer(playerID);

        for (Position coord : coords) {
            if (GameUtils.distance(player.getPosition(), coord) > player.getHarvestRadius()) {
                engineLogger.severe(String.format("Player %d attempted to harvest plant from outside harvest radius: %s", playerID + 1, coord));
                return;
            }
        }

        for (Position coord : coords) {
            Tile tile = state.getTileMap().get(coord);
            if (tile.getCrop().getType() == CropType.NONE) {
                // TODO: should this be an error (failed action)?
                engineLogger.info(String.format("Player %d attempted to harvest where no crop was found: %s", playerID + 1, coord));
            } else {
                player.harvest(tile);
                engineLogger.info(String.format("Player %d harvested %s from %s",
                        playerID + 1, tile.getCrop().getType(), coord));
            }
        }
    }
}

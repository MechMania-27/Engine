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
        Player player = state.getPlayer(playerID);
        Position curPosition = player.getPosition();

        int curCropCount = player.getHarvestedCrops().size();

        for (Position coord : coords) {
            if (GameUtils.distance(curPosition, coord) > player.getHarvestRadius()) {
                engineLogger.severe(
                                    String.format(
                                                "Player %d failed to harvest at location %s, too far!",
                                                playerID + 1,
                                                coord));
            }

            if (curCropCount == player.getCarryingCapacity()) {
                break;
            }

            Tile target = state.getTileMap().getTile(coord);
            if (target.getCrop().getType() == CropType.NONE || target.getCrop().getGrowthTimer() > 0) {
                continue;
            }

            player.getHarvestedCrops().add(target.getCrop());
            curCropCount++;

            target.setCrop(new Crop(CropType.NONE));
        }
    }
}

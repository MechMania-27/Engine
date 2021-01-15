package mech.mania.engine.model.decisions;

import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;
import mech.mania.engine.logging.JsonLogger;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantAction extends PlayerDecision {
    private ArrayList<Position> coords;
    private ArrayList<CropType> cropTypes;

    public PlantAction(int playerID){
        this.playerID = playerID;
        this.coords = new ArrayList<>();
        this.cropTypes = new ArrayList<>();
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<crop>[a-z|A-Z]+)" + separatorRegEx + "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        coords = new ArrayList<>();
        cropTypes = new ArrayList<>();

        // Command must have at least one result
        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match Plant regex");
        }

        do {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            coords.add(new Position(x, y));
            cropTypes.add(CropType.getEnum(matcher.group("crop")));
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        // will use playerID to get the Player object from state and then validate each planting action
        Player player = state.getPlayer(playerID);

        HashMap<CropType, Integer> cropsToPlant = new HashMap<>();
        for (CropType type : CropType.values()) {
            cropsToPlant.put(type, 0);
        }

        for (int i = 0; i < cropTypes.size(); i++) {
            if (GameUtils.distance(player.getPosition(), coords.get(i)) > player.getPlantingRadius()) {
                engineLogger.severe(String.format("Player %d is trying to plant at a position (%s) farther than planting radius (%d > %d)",
                        playerID + 1, coords.get(i), GameUtils.distance(player.getPosition(), coords.get(i)),
                        player.getPlantingRadius()));
                return;
            }
            cropsToPlant.put(cropTypes.get(i), cropsToPlant.get(cropTypes.get(i)) + 1);
        }

        for (CropType type : cropsToPlant.keySet()) {
            if (cropsToPlant.get(type) > player.getSeeds().get(type)) {
                engineLogger.severe(String.format("Player %d is trying to plant more seeds (%d, %s) than they have in their inventory (%d)",
                        playerID + 1, cropsToPlant.get(type), type, player.getSeeds().get(type)));
                return;
            }
        }

        for (int i = 0; i < cropTypes.size(); i++) {
            if (state.getTileMap().get(coords.get(i)).getCrop().getType() == CropType.NONE) {
                state.getTileMap().plantCrop(coords.get(i), cropTypes.get(i));
                player.removeSeeds(cropTypes.get(i), 1);

                engineLogger.info(String.format("Planted %s at %s",
                        cropTypes.get(i), coords.get(i)));
            } else {
                engineLogger.severe(String.format("Player %d attempted to plant on tile with plant, rejecting",
                        playerID + 1));
            }

        }
    }
}

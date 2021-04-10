package mech.mania.engine.model.decisions;

import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;
import mech.mania.engine.logging.JsonLogger;


import java.util.ArrayList;
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
            try {
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                coords.add(new Position(x, y));
                cropTypes.add(CropType.getEnum(matcher.group("crop")));
            } catch (NumberFormatException e) {
                // will occur if input can't be parsed into an int (ex: Integer.MAX_VALUE + 1)
                throw new PlayerDecisionParseException("Arguments did not match Plant regex (did you pass too big an int?)");
            }
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        // will use playerID to get the Player object from state and then validate each planting action
        Player player = state.getPlayer(playerID);
        Player opponent = state.getOpponentPlayer(playerID);

        for (int i = 0; i < cropTypes.size(); i++) {
            if (GameUtils.distance(player.getPosition(), coords.get(i)) > player.getPlantingRadius()) {
                engineLogger.severe(
                                String.format(
                                        "Player %d is trying to plant at a position (%s) farther than planting radius (%d > %d)",
                                        playerID + 1,
                                        coords.get(i),
                                        GameUtils.distance(player.getPosition(), coords.get(i)),
                                        player.getPlantingRadius()));
                continue;
            }

            if (player.getSeeds().get(cropTypes.get(i)) == 0) {
                engineLogger.severe(
                                String.format(
                                        "Player %d failed to plant %s string at %s, not enough",
                                        playerID + 1,
                                        cropTypes.get(i),
                                        coords.get(i)));
                continue;

            }

            if (state.getTileMap().get(coords.get(i)).getCrop().getType() != CropType.NONE) {
                engineLogger.severe(
                                String.format(
                                            "Player %d attempted to plant at %s with plant, rejecting",
                                            playerID + 1,
                                            coords.get(i)));
                continue;
            }

            if (GameUtils.distance(opponent.getPosition(), coords.get(i)) <= opponent.getProtectionRadius()) {
                engineLogger.severe(
                                String.format(
                                            "Player %d attempted to plant at %s inside opponent's protection radius",
                                            playerID + 1,
                                            coords.get(i)));
                continue;
            }

            Tile target = state.getTileMap().get(coords.get(i));
            if (target.isScarecrowEffect() >= 0 && target.isScarecrowEffect() != playerID) {
                engineLogger.severe(
                        String.format(
                                "Player %d attempted to harvest at %s inside opponent's scarecrow radius",
                                playerID + 1,
                                coords.get(i)
                        )
                );
                continue;
            }

            state.getTileMap().plantCrop(coords.get(i), cropTypes.get(i), player);
            //update achievements
            if(cropTypes.get(i) != CropType.JOGANFRUIT && cropTypes.get(i) != CropType.DUCHAMFRUIT&& cropTypes.get(i) != CropType.GRAPE) {
                player.getAchievements().fruit();
            }
            player.removeSeeds(cropTypes.get(i), 1);

            engineLogger.info(
                            String.format(
                                        "Player %d planted %s at %s",
                                        playerID + 1,
                                        cropTypes.get(i), coords.get(i)));
        }
    }
}

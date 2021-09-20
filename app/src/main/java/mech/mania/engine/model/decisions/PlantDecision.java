package mech.mania.engine.model.decisions;

import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;
import mech.mania.engine.logging.JsonLogger;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantDecision extends PlayerDecision {
    protected ArrayList<Position> coords;
    protected ArrayList<CropType> cropTypes;

    public PlantDecision(int playerID, JsonLogger playerLogger, JsonLogger engineLogger){
        super(playerLogger, engineLogger);
        this.playerID = playerID;
        this.coords = new ArrayList<>();
        this.cropTypes = new ArrayList<>();
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<crop>[a-zA-Z_-]+)" + separatorRegEx + "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        coords = new ArrayList<>();
        cropTypes = new ArrayList<>();

        // Command must have at least one result
        if (!matcher.find()) {
            String message = "Arguments did not match Plant regex";
            playerLogger.feedback(message);
            throw new PlayerDecisionParseException(message);
        }

        do {
            try {
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                coords.add(new Position(x, y));
                cropTypes.add(CropType.getEnum(matcher.group("crop")));
            } catch (NumberFormatException e) {
                // will occur if input can't be parsed into an int (ex: Integer.MAX_VALUE + 1)
                String message = "Arguments did not match Plant regex (did you pass too big an int?)";
                playerLogger.feedback(message);
                throw new PlayerDecisionParseException(message);
            }
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state) {
        // will use playerID to get the Player object from state and then validate each planting action
        Player player = state.getPlayer(playerID);
        Player opponent = state.getOpponentPlayer(playerID);

        for (int i = 0; i < cropTypes.size(); i++) {
            if (GameUtils.distance(player.getPosition(), coords.get(i)) > player.getPlantingRadius()) {
                String message = String.format("Trying to plant at a position (%s) farther than planting radius (%d > %d)",
                        coords.get(i),
                        GameUtils.distance(player.getPosition(), coords.get(i)),
                        player.getPlantingRadius());
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                continue;
            }

            if (player.getSeeds().get(cropTypes.get(i)) == 0) {
                String message = String.format("Failed to plant %s string at %s, not enough seeds",
                        cropTypes.get(i), coords.get(i));
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                continue;

            }

            if (state.getTileMap().get(coords.get(i)).getCrop().getType() != CropType.NONE) {
                String message = String.format("Attempted to plant at %s with plant, rejecting", coords.get(i));
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                continue;
            }

            if (GameUtils.distance(opponent.getPosition(), coords.get(i)) <= opponent.getProtectionRadius()) {
                String message = String.format("Attempted to plant at %s inside opponent's protection radius", coords.get(i));
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                continue;
            }

            Tile target = state.getTileMap().get(coords.get(i));
            if (target.isScarecrowEffect() >= 0 && target.isScarecrowEffect() != playerID) {
                String message = String.format("Attempted to harvest at %s inside opponent 's scarecrow radius", coords.get(i));
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                continue;
            }

            state.getTileMap().plantCrop(coords.get(i), cropTypes.get(i), player);
            //update achievements
            if(cropTypes.get(i) != CropType.JOGAN_FRUIT && cropTypes.get(i) != CropType.DUCHAM_FRUIT && cropTypes.get(i) != CropType.GRAPE) {
                player.getAchievements().fruit();
            }
            player.removeSeeds(cropTypes.get(i), 1);

            player.getAchievements().plant();

            String message = String.format("Planted %s at %s", cropTypes.get(i), coords.get(i));
            playerLogger.feedback(message);
            engineLogger.info(String.format("Player %d: " + message, playerID + 1));
        }
    }
}

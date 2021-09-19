package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.util.GameUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HarvestAction extends PlayerDecision {
    protected ArrayList<Position> coords;

    public HarvestAction(int playerID, JsonLogger playerLogger, JsonLogger engineLogger){
        super(playerLogger, engineLogger);
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
            String message = "Arguments did not match Harvest regex";
            playerLogger.feedback(message);
            throw new PlayerDecisionParseException(message);
        }

        do {
            try {
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                coords.add(new Position(x, y));
            } catch (NumberFormatException e) {
                // will occur if input can't be parsed into an int (ex: Integer.MAX_VALUE + 1)
                String message = "Arguments did not match Harvest regex (did you pass too big an int?)";
                playerLogger.feedback(message);
                throw new PlayerDecisionParseException(message);
            }
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state) {
        Player player = state.getPlayer(playerID);
        Player opponent = state.getOpponentPlayer(playerID);
        Position curPosition = player.getPosition();

        int curCropCount = player.getHarvestedCrops().size();

        for (Position coord : coords) {
            if (GameUtils.distance(curPosition, coord) > player.getHarvestRadius()) {
                String message = String.format("Failed to harvest at %s outside of harvest radius %d",
                        coord,
                        player.getHarvestRadius());
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1) + message);
                continue;
            }

            if (curCropCount == player.getCarryingCapacity()) {
                String message = String.format("Attempted to harvest at %s, more crops than carrying capacity %d",
                        coord,
                        player.getCarryingCapacity());
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1) + message);
                break;
            }

            Tile target = state.getTileMap().get(coord);
            if (target.getCrop().getType() == CropType.NONE) {
                String message = String.format("Attempted to harvest where no crop was found at %s", coord);
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1) + message);
                continue;
            }

            if (target.getCrop().getGrowthTimer() > 0) {
                String message = String.format("Attempted to harvest an unripe crop at %s", coord);
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1));
                continue;
            }

            if (GameUtils.distance(opponent.getPosition(), coord) <= opponent.getProtectionRadius()) {
                String message = String.format("Attempted to harvest at %s inside opponent's protection radius", coord);
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1));
                continue;
            }

            if (target.isScarecrowEffect() >= 0 && target.isScarecrowEffect() != playerID) {
                String message = String.format("Attempted to harvest at %s inside opponent's scarecrow radius", coord);
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: ", playerID + 1));
                continue;
            }

            String message = String.format("Harvested crop %s from %s", target.getCrop().getType(), coord);
            playerLogger.feedback(message);
            engineLogger.info(String.format("Player %d: ", playerID + 1));

            //update achievements
            Achievements achievements = player.getAchievements();
            if (target.getPlanter() != player) {
                achievements.steal();
                engineLogger.debug(String.format("Player %d: Achievement: steal", playerID + 1));
            }
            if (target.getCrop().getType() == CropType.GRAPE) {
                achievements.stealGrapes(1);
                engineLogger.debug(String.format("Player %d: Achievement: steal grapes + 1", playerID + 1));
            }
            if (target.getCrop().getType() != CropType.JOGAN_FRUIT && target.getCrop().getType() != CropType.DUCHAM_FRUIT && target.getCrop().getType() != CropType.GRAPE) {
                achievements.fruit();
                engineLogger.debug(String.format("Player %d: Achievement: fruit", playerID + 1));
            }
            player.harvest(target);
            curCropCount++;

        }
    }
}

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
            try {
                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));
                coords.add(new Position(x, y));
            } catch (NumberFormatException e) {
                // will occur if input can't be parsed into an int (ex: Integer.MAX_VALUE + 1)
                throw new PlayerDecisionParseException("Arguments did not match Harvest regex (did you pass too big an int?)");
            }
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        Player player = state.getPlayer(playerID);
        Player opponent = state.getOpponentPlayer(playerID);
        Position curPosition = player.getPosition();

        int curCropCount = player.getHarvestedCrops().size();

        for (Position coord : coords) {
            if (GameUtils.distance(curPosition, coord) > player.getHarvestRadius()) {
                engineLogger.severe(
                                    String.format(
                                            "Player %d failed to harvest at %s outside of harvest radius %d",
                                            playerID + 1,
                                            coord,
                                            player.getHarvestRadius()));
            }

            if (curCropCount == player.getCarryingCapacity()) {
                engineLogger.severe(
                                    String.format(
                                                "Player %d attempted to harvest at %s, more crops than carrying capacity %d",
                                                playerID + 1,
                                                coord,
                                                player.getCarryingCapacity()));
                break;
            }

            Tile target = state.getTileMap().getTile(coord);
            if (target.getCrop().getType() == CropType.NONE) {
                engineLogger.severe(
                        String.format(
                                "Player %d attempted to harvest where no crop was found at %s",
                                playerID + 1,
                                coord
                        )
                );
                continue;
            }

            if (target.getCrop().getGrowthTimer() > 0) {
                engineLogger.severe(
                        String.format(
                                "Player %d attempted to harvest an unripe crop at %s",
                                playerID + 1,
                                coord
                        )
                );
                continue;
            }

            if (GameUtils.distance(opponent.getPosition(), coord) <= opponent.getProtectionRadius()) {
                engineLogger.severe(
                        String.format(
                                "Player %d attempted to harvest at %s inside opponent's protection radius",
                                playerID + 1,
                                coord
                        )
                );
                continue;
            }

            if (target.isScarecrowEffect() >= 0 && target.isScarecrowEffect() != playerID) {
                engineLogger.severe(
                        String.format(
                                "Player %d attempted to harvest at %s inside opponent's scarecrow radius",
                                playerID + 1,
                                coord
                        )
                );
                continue;
            }

            engineLogger.info(
                    String.format(
                            "Player %d harvested crop %s from %s",
                            playerID + 1,
                            target.getCrop().getType(),
                            coord
                    )
            );

            //update achievements
            achievements = player.getAchievements();
            if(target.getPlanter() != player) {
                achievements.steal();
            }
            if(target.getCrop().getType() == CropType.GRAPE) {
                achievements.stealGrapes();
            }
            if(target.getCrop().getType() != CropType.JORGANFRUIT && target.getCrop().getType() != CropType.DUCHAMFRUIT) {
                achievements.fruit();
            }
            player.harvest(target);
            curCropCount++;

        }
    }
}

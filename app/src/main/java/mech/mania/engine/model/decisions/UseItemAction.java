package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UseItemAction extends PlayerDecision {

    public UseItemAction(int playerID, JsonLogger playerLogger, JsonLogger engineLogger) {
        super(playerLogger, engineLogger);
        this.playerID = playerID;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<item>[a-z|A-Z]+)" + separatorRegEx + "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);


        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match UseItem regex");
        }
        return this;
    }

    public void performAction(GameState state) {
        Position loc = state.getPlayer(playerID).getPosition();
        ItemType item = state.getPlayer(playerID).getItem();
        TileMap map = state.getTileMap();
        Player player = state.getPlayer(playerID);

        switch (item) {
            case NONE:
                engineLogger.severe(
                                String.format(
                                        "An item was never specified by player %d",
                                        playerID + 1
                                )
                );
                break;

            case PESTICIDE:
                // TODO make radius inference dynamic instead of hardcoded 1 ring
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).setPesticideEffect(true);
                            map.get(loc.getX() + i, loc.getY() + j).getCrop().applyPesticide();
                        }
                    }
                }
                break;

            case FERTILITY_IDOL:
                // TODO make radius inference dynamic instead of hardcoded 2 ring
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).setFertilityIdolEffect(true);
                        }
                    }
                }
                break;

            case RAIN_TOTEM:
                // TODO make radius inference dynamic instead of hardcoded 2 ring
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).setRainTotemEffect(true);
                        }
                    }
                }
                break;

            case SCARECROW:
                // TODO make radius inference dynamic instead of hardcoded 2 ring
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            if(map.get(loc.getX() + i, loc.getY() + j).isScarecrowEffect() == 1 - playerID) {
                                player.getAchievements().addAchievement("Ornithophobia");
                                //System.out.println("debug");
                            }
                            map.get(loc.getX() + i, loc.getY() + j).setScarecrowEffect(playerID);
                        }
                    }
                }
                break;
        }

        if (playerID == 0) {
            map.get(loc).setP1Item(item);
        } else {
            map.get(loc).setP2Item(item);
        }

        engineLogger.info(
                        String.format(
                                "Player %d placed %s at %s",
                                playerID + 1,
                                item,
                                loc));
    }
}

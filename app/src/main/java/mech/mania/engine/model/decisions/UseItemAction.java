package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
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
        String regex = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);


        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match UseItem regex");
        }
        return this;
    }

    public void performAction(GameState state) {
        if (state.getPlayer(playerID).getUsedItem()) {
            String message = "Item was already used";
            playerLogger.feedback(message);
            engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
            return;
        }

        Position loc = state.getPlayer(playerID).getPosition();
        ItemType item = state.getPlayer(playerID).getItem();
        TileMap map = state.getTileMap();
        Player player = state.getPlayer(playerID);
        
        Config config = player.getConfig();

        switch (item) {
            case NONE:
                String message = "An item was never specified";
                playerLogger.feedback(message);
                engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                break;

            case PESTICIDE:
                int pesticideEffectRadius = config.PESTICIDE_EFFECT_RADIUS;
                for (int i = -pesticideEffectRadius; i <= pesticideEffectRadius; i++) {
                    for (int j = -pesticideEffectRadius; j <= pesticideEffectRadius; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).getCrop().applyPesticide();
                        }
                    }
                }
                break;

            case FERTILITY_IDOL:
                int fertilityIdolEffectRadius = config.FERTILITY_IDOL_EFFECT_RADIUS;
                for (int i = -fertilityIdolEffectRadius; i <= fertilityIdolEffectRadius; i++) {
                    for (int j = -fertilityIdolEffectRadius; j <= fertilityIdolEffectRadius; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).setFertilityIdolEffect(true);
                        }
                    }
                }
                break;

            case RAIN_TOTEM:
                int rainTotemEffectRadius = config.RAIN_TOTEM_EFFECT_RADIUS;
                for (int i = -rainTotemEffectRadius; i <= rainTotemEffectRadius; i++) {
                    for (int j = -rainTotemEffectRadius; j <= rainTotemEffectRadius; j++) {
                        if (map.isValidPosition(loc.getX() + i, loc.getY() + j)) {
                            map.get(loc.getX() + i, loc.getY() + j).setRainTotemEffect(true);
                        }
                    }
                }
                break;

            case SCARECROW:
                int scarecrowEffectRadius = config.SCARECROW_EFFECT_RADIUS;
                for (int i = -scarecrowEffectRadius; i <= scarecrowEffectRadius; i++) {
                    for (int j = -scarecrowEffectRadius; j <= scarecrowEffectRadius; j++) {
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
            case COFFEE_THERMOS:
                state.getPlayer(playerID).setHasCoffeeThermos(true);
                break;
            case DELIVERY_DRONE:
                state.getPlayer(playerID).setDeliveryDrone(true);
                break;
        }

        if (playerID == 0) {
            map.get(loc).setP1Item(item);
        } else {
            map.get(loc).setP2Item(item);
        }

        state.getPlayer(playerID).setUsedItem();

        String message = String.format("Placed %s at %s", item, loc);
        playerLogger.feedback(message);
        engineLogger.info(String.format("Player %d: " + message, playerID + 1));
    }
}

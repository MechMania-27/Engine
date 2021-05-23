package mech.mania.engine.core;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.model.decisions.MoveAction;
import mech.mania.engine.model.decisions.PlayerDecision;

public class GameLogic {
    public static boolean isGameOver(GameState gameState, Config gameConfig) {
        // Game is over when fertility band is off the map. Equivalent to all soil tiles being ARID
        return gameState.getTurn() / gameConfig.F_BAND_MOVE_DELAY >
                gameState.getTileMap().getMapHeight() + 2 * gameConfig.F_BAND_OUTER_HEIGHT +
                2 * gameConfig.F_BAND_MID_HEIGHT + gameConfig.F_BAND_INNER_HEIGHT;
    }

    public static GameState updateGameState(GameState gameState,
                                            PlayerDecision player1Decision,
                                            PlayerDecision player2Decision,
                                            Config gameConfig,
                                            JsonLogger engineLogger) {
        GameState newGameState = new GameState(gameState);
        newGameState.setTurn(gameState.getTurn() + 1);

        // Perform non-movement actions
        if (! (player1Decision instanceof MoveAction)) {
            player1Decision.performAction(newGameState, engineLogger);
        }
        if (! (player2Decision instanceof MoveAction)) {
            player2Decision.performAction(newGameState, engineLogger);
        }

        // Grow crops
        newGameState.getTileMap().growCrops();

        // Fertility band movement
        newGameState.getTileMap().setFertilityBand(newGameState.getTurn());

        // Wither crops in ARID land
        for (Tile tile : newGameState.getTileMap()) {
            if (tile.getType() == TileType.ARID) {
                tile.getCrop().setGrowthTimer(0);
                tile.getCrop().setValue(0);
                if(tile.getCrop().getType() != CropType.NONE) {
                    tile.getPlanter().getAchievements().destroyCrops(1);
                }

            }
        }

        return newGameState;
    }
}

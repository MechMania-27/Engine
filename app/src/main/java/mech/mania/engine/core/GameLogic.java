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

    public static void setWinners(GameLog gameLog, GameState finalGameState, JsonLogger engineLogger) {
        // at this point, the game has ended, so no need to consider crashes
        Player player1 = finalGameState.getPlayer1();
        Player player2 = finalGameState.getPlayer2();
        if (player1.getMoney() > player2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.WON);
            gameLog.setPlayer2EndState(PlayerEndState.LOST);
            engineLogger.info(String.format("Player 1 has won due to having more money (%.2f > %.2f)", player1.getMoney(), player2.getMoney()));
        } else if (player1.getMoney() < player2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.LOST);
            gameLog.setPlayer2EndState(PlayerEndState.WON);
            engineLogger.info(String.format("Player 2 has won due to having more money (%.2f > %.2f)", player2.getMoney(), player1.getMoney()));
        } else {
            gameLog.setPlayer1EndState(PlayerEndState.TIED);
            gameLog.setPlayer2EndState(PlayerEndState.TIED);
            engineLogger.info(String.format("Both players ended up with the same amount of money: %.2f", player1.getMoney()));
        }
    }

    public static GameState movePlayer(GameState gameState,
                                       MoveAction player1Decision,
                                       MoveAction player2Decision,
                                       JsonLogger engineLogger) {
        GameState newGameState = new GameState(gameState);
        player1Decision.performAction(newGameState, engineLogger);
        player2Decision.performAction(newGameState, engineLogger);
        return newGameState;
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
                tile.getPlanter().getAchievements().destroyCrop();
            }
        }

        return newGameState;
    }
}

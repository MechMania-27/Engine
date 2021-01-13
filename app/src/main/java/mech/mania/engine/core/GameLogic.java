package mech.mania.engine.core;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.*;
import mech.mania.engine.model.decisions.MoveAction;
import mech.mania.engine.model.decisions.PlayerDecision;

public class GameLogic {
    public static boolean isGameOver(GameState gameState) {
        Config config = gameState.getGameConfig();

        // Game is over when fertility band is off the map. Equivalent to all soil tiles being ARID
        return gameState.getTurn() / config.F_BAND_MOVE_DELAY >
                gameState.getTileMap().getMapHeight() + 2 * config.F_BAND_OUTER_HEIGHT +
                2 * config.F_BAND_MID_HEIGHT + config.F_BAND_INNER_HEIGHT;
    }

    public static void setWinners(GameLog gameLog, GameState finalGameState) {
        // at this point, the game has ended, so no need to consider crashes
        Player player1 = finalGameState.getPlayer1();
        Player player2 = finalGameState.getPlayer2();
        if (player1.getMoney() > player2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.WON);
            gameLog.setPlayer2EndState(PlayerEndState.LOST);
        } else if (player1.getMoney() < player2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.LOST);
            gameLog.setPlayer2EndState(PlayerEndState.WON);
        } else {
            gameLog.setPlayer1EndState(PlayerEndState.TIE);
            gameLog.setPlayer2EndState(PlayerEndState.TIE);
        }
    }

    public static GameState movePlayer(GameState gameState,
                                       MoveAction player1Decision,
                                       MoveAction player2Decision) {
        GameState newGameState = new GameState(gameState);
        player1Decision.performAction(newGameState);
        player2Decision.performAction(newGameState);
        return newGameState;
    }

    public static GameState updateGameState(GameState gameState,
                                            PlayerDecision player1Decision,
                                            PlayerDecision player2Decision) {
        GameState newGameState = new GameState(gameState);
        newGameState.setTurn(gameState.getTurn() + 1);

        // Perform non-movement actions
        if (! (player1Decision instanceof MoveAction)) {
            player1Decision.performAction(newGameState);
        }
        if (! (player2Decision instanceof MoveAction)) {
            player2Decision.performAction(newGameState);
        }

        // Grow crops
        newGameState.getTileMap().growCrops();

        // Fertility band movement
        newGameState.getTileMap().setFertilityBand(newGameState.getTurn());

        return newGameState;
    }
}

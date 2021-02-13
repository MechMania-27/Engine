package mech.mania.engine.core;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;

public class GameLogic {
    public static boolean isGameOver(GameState gameState) {
        return gameState.getTurn() == 100;
    }

    public static GameState updateGameState(GameState gameState,
                                            PlayerDecision player1Decision,
                                            PlayerDecision player2Decision) {
        GameState newGameState = new GameState(gameState);
        newGameState.setTurn(gameState.getTurn() + 1);
        return newGameState;
    }
}

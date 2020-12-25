package mech.mania.engine.core;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;

public class GameLogic {
    public static boolean isGameOver(GameState gameState) {
        return false;
    }

    public static GameState updateGameState(GameState gameState, PlayerDecision player1Decision, PlayerDecision player2Decision) {
        return new GameState(gameState);
    }

    public static Winner getWinner(GameState gameState) {
        return Winner.TIE;
    }
}

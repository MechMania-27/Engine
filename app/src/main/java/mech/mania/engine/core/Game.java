package mech.mania.engine.core;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.decisions.MoveAction;
import mech.mania.engine.model.decisions.PlayerDecision;
import mech.mania.engine.networking.PlayerCommunicationInfo;

import java.io.IOException;
import java.util.List;

public class Game {

    private final Config gameConfig;
    private final PlayerCommunicationInfo player1;
    private final PlayerCommunicationInfo player2;
    private final JsonLogger engineLogger;
    private final GameLog gameLog = new GameLog();

    public Game(Config gameConfig, PlayerCommunicationInfo player1, PlayerCommunicationInfo player2, JsonLogger engineLogger) {
        this.gameConfig = gameConfig;
        this.player1 = player1;
        this.player2 = player2;
        this.engineLogger = engineLogger;
    }

    /**
     * This function will run the game until the game is over and return the winner.
     * This is mostly an attempt to uncrowd the main function.
     * @return GameLog GameLog object that represents the final state after the game ends
     */
    public GameLog run() {
        GameState gameState = new GameState(gameConfig,
                player1.getPlayerName(), player1.getStartingItem(), player1.getStartingUpgrade(),
                player2.getPlayerName(), player2.getStartingItem(), player2.getStartingUpgrade());

        do {
            gameState = singleTurn(gameState);
        } while (gameState != null && !GameLogic.isGameOver(gameState, gameConfig));

        if (gameState == null) {
            engineLogger.info("Game ended due to a player error");
            return gameLog;
        }

        System.out.printf("Player 1: $%.2f, Player 2: $%.2f\n",
                gameState.getPlayer1().getMoney(), gameState.getPlayer2().getMoney());

        Player finalPlayer1 = gameState.getPlayer1();
        Player finalPlayer2 = gameState.getPlayer2();
        if (finalPlayer1.getMoney() > finalPlayer2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.WON);
            gameLog.setPlayer2EndState(PlayerEndState.LOST);
            engineLogger.info(String.format("Player 1 has won due to having more money (%.2f > %.2f)", finalPlayer1.getMoney(), finalPlayer2.getMoney()));
        } else if (finalPlayer1.getMoney() < finalPlayer2.getMoney()) {
            gameLog.setPlayer1EndState(PlayerEndState.LOST);
            gameLog.setPlayer2EndState(PlayerEndState.WON);
            engineLogger.info(String.format("Player 2 has won due to having more money (%.2f > %.2f)", finalPlayer2.getMoney(), finalPlayer1.getMoney()));
        } else {
            gameLog.setPlayer1EndState(PlayerEndState.TIED);
            gameLog.setPlayer2EndState(PlayerEndState.TIED);
            engineLogger.info(String.format("Both players ended up with the same amount of money: %.2f", finalPlayer1.getMoney()));
        }

        //Display achievements
        displayAchievements(gameState);
        return gameLog;
    }

    /**
     * A function representing the logic that will happen for a single turn of the game
     * @param gameState current game state
     * @return new game state
     */
    private GameState singleTurn(GameState gameState) {
        long startTime = System.nanoTime();

        // send game states
        PlayerEndState player1EndState = null;
        PlayerEndState player2EndState = null;

        try {
            player1.sendGameState(gameState);
            // engineLogger.debug("Sent player 1 a game state");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Error while sending game state to player 1: ", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            player2.sendGameState(gameState);
            // engineLogger.debug("Sent player 2 a game state");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Error while sending game state to player 2", e);
            player2EndState = PlayerEndState.ERROR;
        }

        if (player1EndState != null || player2EndState != null) {
            if (player1EndState == PlayerEndState.ERROR && player2EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            } else if (player1EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.WON);
            } else {
                gameLog.setPlayer1EndState(PlayerEndState.WON);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            }
            return null;
        }

        // add game states to list of total game states
        gameLog.addState(new GameState(gameState));

        player1EndState = null;
        player2EndState = null;
        PlayerDecision player1Decision = null;
        PlayerDecision player2Decision = null;

        try {
            player1Decision = player1.getPlayerDecision();
            // engineLogger.debug("Got player 1's decision");
        } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
            engineLogger.severe("Error while getting move action from player 1", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            player2Decision = player2.getPlayerDecision();
            // engineLogger.debug("Got player 2's decision");
        } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
            engineLogger.severe("Error while getting move action from player 2", e);
            player2EndState = PlayerEndState.ERROR;
        }

        if (player1EndState != null || player2EndState != null) {
            if (player1EndState == PlayerEndState.ERROR && player2EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            } else if (player1EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.WON);
            } else {
                gameLog.setPlayer1EndState(PlayerEndState.WON);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            }
            return null;
        }

        // move the players based on move decisions
        boolean validPlayer1MoveAction = true;
        boolean validPlayer2MoveAction = true;
        if (player1Decision instanceof MoveAction && player2Decision instanceof MoveAction) {
            engineLogger.debug("Both players submitted a move decision");
            GameState newGameState = new GameState(gameState);
            player1Decision.performAction(newGameState, engineLogger);
            player2Decision.performAction(newGameState, engineLogger);
        } else if (player1Decision instanceof MoveAction) {
            engineLogger.debug("Player 2 did not submit a move decision");
            // player 2 submitted a non-move decision, so store the decision
            GameState newGameState = new GameState(gameState);
            player1Decision.performAction(newGameState, engineLogger);
            player2Decision.performAction(newGameState, engineLogger);
            validPlayer2MoveAction = false;
        } else if (player2Decision instanceof MoveAction) {
            engineLogger.debug("Player 1 did not submit a move decision");
            // player 1 submitted a non-move decision, so store the decision
            GameState newGameState = new GameState(gameState);
            player1Decision.performAction(newGameState, engineLogger);
            player2Decision.performAction(newGameState, engineLogger);
            validPlayer1MoveAction = false;
        } else {
            engineLogger.debug("Both players did not submit a move decision");
            validPlayer1MoveAction = false;
            validPlayer2MoveAction = false;
        }

        // send the players another game state after moving
        try {
            player1.sendGameState(gameState);
            // engineLogger.debug("Sent player 1 a game state");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Error while sending game state to player 1: ", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            player2.sendGameState(gameState);
            // engineLogger.debug("Sent player 2 a game state");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Error while sending game state to player 2", e);
            player2EndState = PlayerEndState.ERROR;
        }

        if (player1EndState != null || player2EndState != null) {
            if (player1EndState == PlayerEndState.ERROR && player2EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            } else if (player1EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.WON);
            } else {
                gameLog.setPlayer1EndState(PlayerEndState.WON);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            }
            return null;
        }

        // retrieve action decisions from players
        player1EndState = null;
        player2EndState = null;

        if (validPlayer1MoveAction) {
            try {
                player1Decision = player1.getPlayerDecision();
                // engineLogger.debug("Got player 1's action decision");
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                engineLogger.severe("Error while getting player decision from player 1", e);
                player1EndState = PlayerEndState.ERROR;
            }
        } else {
            engineLogger.debug("Player 1 did not submit a move decision (probably an action decision), skipping getting decision");
        }

        if (validPlayer2MoveAction) {
            try {
                player2Decision = player2.getPlayerDecision();
                // engineLogger.debug("Got player 2's action decision");
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                engineLogger.severe("Error while getting player decision from player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }
        } else {
            engineLogger.debug("Player 2 did not submit a move decision (probably an action decision), skipping getting decision");
        }

        if (player1EndState != null || player2EndState != null) {
            if (player1EndState == PlayerEndState.ERROR && player2EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            } else if (player1EndState == PlayerEndState.ERROR) {
                gameLog.setPlayer1EndState(PlayerEndState.ERROR);
                gameLog.setPlayer2EndState(PlayerEndState.WON);
            } else {
                gameLog.setPlayer1EndState(PlayerEndState.WON);
                gameLog.setPlayer2EndState(PlayerEndState.ERROR);
            }
            return null;
        }

        // update game state
        gameState = GameLogic.updateGameState(gameState, player1Decision, player2Decision, gameConfig, engineLogger);
        engineLogger.debug("Updated game state");

        long endTime = System.nanoTime();
        engineLogger.info(String.format("Turn %d took %.2f milliseconds", gameState.getTurn() - 1, (endTime - startTime) / 1e6));

        player1.getLogger().incrementTurn();
        player2.getLogger().incrementTurn();
        engineLogger.incrementTurn();

        return gameState;
    }

    /**
     * Function to display achievements
     * @param gameState current game state
     */
    private void displayAchievements(GameState gameState) {
        boolean p1win = gameState.getPlayer1().getMoney() > gameState.getPlayer2().getMoney();
        List<String> p1achievements = gameState.getPlayer1().getAchievements().getFinalAchievements(p1win, gameConfig.STARTING_MONEY, gameState.getPlayer1().getMoney());
        List<String> p2achievements = gameState.getPlayer2().getAchievements().getFinalAchievements(!p1win, gameConfig.STARTING_MONEY, gameState.getPlayer2().getMoney());
        engineLogger.info("Player 1 has unlocked the following achievements:");
        if(p1achievements.size() == 0) {
            engineLogger.info("Player 1 does not unlock any achievements");
        }
        for (String p1achievement : p1achievements) {
            engineLogger.info(p1achievement);
        }
        engineLogger.info("Player 2 has unlocked the following achievements:");
        if(p2achievements.size() == 0) {
            engineLogger.info("Player 2 does not unlock any achievements");
        }
        for (String p2achievement : p2achievements) {
            engineLogger.info(p2achievement);
        }
    }
}

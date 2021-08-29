package mech.mania.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.decisions.MoveAction;
import mech.mania.engine.model.decisions.PlayerDecision;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


/**
 * Class that runs the game.
 */
public class Main {

    /**
     * The Main function. This will get the command line arguments, create the
     * players via PlayerCommunicationInfo objects, start the game using gameLoop,
     * and call writeListToFile to write the resulting log files to their respective
     * logs (player1, player2, and the game log).
     *
     * @param args Program arguments
     */
    public static void main(String[] args) {
        Config gameConfig;
        try {
            gameConfig = new Config();
        } catch (Exception e) {
            System.err.println("Config file could not be read: " + e.getMessage());
            return;
        }
        CommandLine commandLine = getCommandLineArgs(args, gameConfig);
        if (commandLine == null) {
            return;
        }

        JsonLogger player1Logger = new JsonLogger(0, "player1");
        JsonLogger player2Logger = new JsonLogger(0, "player2");
        JsonLogger engineLogger = new JsonLogger(0, "engine");

        // should the logger print debug statements?
        player1Logger.setDebug(true);
        player2Logger.setDebug(true);
        engineLogger.setDebug(commandLine.hasOption("d"));

        // using the arguments from the command line, package up all necessary
        // arguments into a PlayerCommunicationInfo object
        PlayerCommunicationInfo player1 = new PlayerCommunicationInfo(
                gameConfig, engineLogger, player1Logger, 0,
                commandLine.getOptionValue("n"),
                commandLine.getOptionValue("e"));
        PlayerCommunicationInfo player2 = new PlayerCommunicationInfo(
                gameConfig, engineLogger, player2Logger, 1,
                commandLine.getOptionValue("N"),
                commandLine.getOptionValue("E"));

        PlayerEndState player1EndState = null;
        PlayerEndState player2EndState = null;

        // player process startup
        try {
            engineLogger.debug("Starting player 1 process...");
            player1.start();
            player1.checkHeartbeat();
            engineLogger.debug("Started player 1 process");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Player 1 failed to start", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            engineLogger.debug("Starting player 2 process...");
            player2.start();
            player2.checkHeartbeat();
            engineLogger.debug("Started player 2 process");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Player 2 failed to start", e);
            player2EndState = PlayerEndState.ERROR;
        }

        // TODO: refactor this somehow
        if (player1EndState != null || player2EndState != null) {
            player1EndState = PlayerEndState.ERROR;
            player2EndState = PlayerEndState.ERROR;
        } else {
            engineLogger.info("Successful player initialization");

            try {
                engineLogger.debug("Asking player 1 for starting items");
                player1.askForStartingItems();
                engineLogger.debug("Finished asking player 1 for starting items");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while receiving starting items from player 2: ", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                engineLogger.debug("Asking player 2 for starting items");
                player2.askForStartingItems();
                engineLogger.debug("Finished asking player 2 for starting items");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while receiving starting items from player 2: ", e);
                player2EndState = PlayerEndState.ERROR;
            }

            if (player1EndState != null || player2EndState != null) {
                player1EndState = PlayerEndState.ERROR;
                player2EndState = PlayerEndState.ERROR;
            } else {

                // start the game
                player1Logger.incrementTurn();
                player2Logger.incrementTurn();
                engineLogger.incrementTurn();

                GameLog gameLog = new GameLog();
                gameLoop(gameConfig, gameLog, player1, player2, player1Logger, player2Logger, engineLogger);

                player1EndState = gameLog.getPlayer1EndState();
                player2EndState = gameLog.getPlayer2EndState();

                engineLogger.info("Finished game loop");

                Gson serializer = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
//                    .registerTypeAdapter(GameLog.class, new CustomSerializerGame())

//                    .addSerializationExclusionStrategy(new ExclusionStrategy() {
//
//                        @Override
//                        public boolean shouldSkipField(FieldAttributes f) {
//                            if (f.getDeclaringClass() == Tile.class) {
//                                if (f.getName())
//                            } return false;
////                            return false;
//                        }
//
//                        @Override
//                        public boolean shouldSkipClass(Class<?> clazz) {
//                            return false;
////                            return clazz.getFields()[0].getName().equals("tiles");
//                        }
//                    })
//                    .create();

                // TODO

                String gameLogJson = serializer.toJson(gameLog, GameLog.class);
                writeListToFile(Collections.singletonList(gameLogJson), commandLine.getOptionValue("g", gameConfig.REPLAY_FILENAME), engineLogger);
            }
        }

        player1.stop();
        player2.stop();

        player1Logger.incrementTurn();
        player2Logger.incrementTurn();
        engineLogger.incrementTurn();

        // finish game by writing all log files and replay files
        player1Logger.writeToFile(commandLine.getOptionValue("l", player1.getPlayerName() + gameConfig.PLAYERLOG_EXTENSION));
        player2Logger.writeToFile(commandLine.getOptionValue("L", player2.getPlayerName() + gameConfig.PLAYERLOG_EXTENSION));
        engineLogger.writeToFile(gameConfig.ENGINELOG_FILENAME);

        System.out.println("Game complete. PLAYER1: " + player1EndState + ", PLAYER2: " + player2EndState);
    }

    /**
     * This method will initialize and parse the command line arguments
     * using com.apache.commons.cli.
     * Example program arguments (lowercase corresponds to player1, uppercase to player2)
     * <code>./engine -n "player1" -e "./bot1.py" -N "player2" -E "./bot2.py"</code>
     *
     * @param args         String[] that contains the arguments to the program call
     * @param gameDefaults Config object that contains the game defaults
     * @return a com.apache.commons.cli.CommandLine object that can be used to read parsed values
     */
    private static CommandLine getCommandLineArgs(String[] args, Config gameDefaults) {
        Options options = new Options();

        Options helpOptions = new Options();
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("Show this help message")
                .build();
        helpOptions.addOption(help);
        options.addOption(help);

        // ======================== PLAYER NAMES AND EXECUTABLES =======================
        Option player1Name = Option.builder("n")
                .longOpt("player1-name")
                .hasArg()
                .argName("name")
                .required()
                .desc("First player's name (required)")
                .type(String.class)
                .build();
        options.addOption(player1Name);
        Option player1Executable = Option.builder("e")
                .longOpt("player1-executable")
                .hasArg()
                .argName("command")
                .required()
                .desc("First player's executable as a string (required)")
                .type(String.class)
                .build();
        options.addOption(player1Executable);

        Option player2Name = Option.builder("N")
                .longOpt("player2-name")
                .hasArg()
                .argName("name")
                .required()
                .desc("Second player's name (required)")
                .type(String.class)
                .build();
        options.addOption(player2Name);
        Option player2Executable = Option.builder("E")
                .longOpt("player2-executable")
                .hasArg()
                .argName("command")
                .required()
                .desc("Second player's executable as a string (required)")
                .type(String.class)
                .build();
        options.addOption(player2Executable);

        // ============================ GAME LOGS =================================
        Option replayFileName = Option.builder("r")
                .longOpt("replayfile-name")
                .hasArg()
                .argName("filename")
                .desc("Name of the replay file to be made (for visualizer) " +
                        "(default=" + gameDefaults.REPLAY_FILENAME + ")")
                .type(String.class)
                .build();
        options.addOption(replayFileName);

        Option engineLogFileName = Option.builder("r")
                .longOpt("enginelogfile-name")
                .hasArg()
                .argName("filename")
                .desc("Name of the engine log file to be made " +
                        "(default=" + gameDefaults.ENGINELOG_FILENAME + ")")
                .type(String.class)
                .build();
        options.addOption(engineLogFileName);

        Option player1LogFileName = Option.builder("l")
                .longOpt("player1-logfile-name")
                .hasArg()
                .argName("filename")
                .desc("Name of the player 1 log file to be made (for player) " +
                        "(default=playername.log)")
                .type(String.class)
                .build();
        options.addOption(player1LogFileName);

        Option player2LogFileName = Option.builder("L")
                .longOpt("player2-logfile-name")
                .hasArg()
                .argName("filename")
                .desc("Name of the player 2 log file to be made (for player) " +
                        "(default=playername.log)")
                .type(String.class)
                .build();
        options.addOption(player2LogFileName);

        Option debug = Option.builder("d")
                .longOpt("debug")
                .desc("Engine debug statements should be printed out to the log")
                .type(boolean.class)
                .build();
        options.addOption(debug);

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            CommandLine commandLineHelp = parser.parse(helpOptions, args, true);
            if (commandLineHelp.hasOption("h") || commandLineHelp.hasOption("help") || commandLineHelp.getArgs().length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Engine <args>", options);
            } else {
                commandLine = parser.parse(options, args);
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse command line arguments: " + e.getMessage());
        }

        return commandLine;
    }

    /**
     * This function will run the game until the game is over and return the winner.
     * This is mostly an attempt to uncrowd the main function.
     *
     * @param gameConfig Config object that contains game default values
     * @param gameLog    GameLog object that contains the running list of GameState objects. Should be empty, will be filled
     * @param player1    PlayerCommunicationInfo object that keeps information about communication with player 1
     * @param player2    PlayerCommunicationInfo object that keeps information about communication with player 2
     */
    protected static void gameLoop(Config gameConfig, GameLog gameLog,
                                   PlayerCommunicationInfo player1,
                                   PlayerCommunicationInfo player2,
                                   JsonLogger player1Logger, JsonLogger player2Logger,
                                   JsonLogger engineLogger) {
        GameState gameState = new GameState(gameConfig,
                player1.getPlayerName(), player1.getStartingItem(), player1.getStartingUpgrade(),
                player2.getPlayerName(), player2.getStartingItem(), player2.getStartingUpgrade());

        PlayerEndState player1EndState;
        PlayerEndState player2EndState;

        do {
            long startTime = System.nanoTime();

            // send game states
            player1EndState = null;
            player2EndState = null;

            try {
                engineLogger.debug("Sending player 1's game state...");
                player1.sendGameState(gameState);
                engineLogger.debug("Sent player 1 a game state");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while sending game state to player 1: ", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                engineLogger.debug("Sending player 2's game state...");
                player2.sendGameState(gameState);
                engineLogger.debug("Sent player 2 a game state");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while sending game state to player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }

            if (badEndState(gameLog, player1EndState, player2EndState)) return;

            // add game states to list of total game states
            gameLog.addState(new GameState(gameState));

            player1EndState = null;
            player2EndState = null;
            PlayerDecision player1Decision = null;
            PlayerDecision player2Decision = null;

            try {
                engineLogger.debug("Getting player 1's decision...");
                player1Decision = player1.getPlayerDecision();
                engineLogger.debug("Got player 1's decision");
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                engineLogger.severe("Error while getting move action from player 1", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                engineLogger.debug("Getting player 2's decision...");
                player2Decision = player2.getPlayerDecision();
                engineLogger.debug("Got player 2's decision");
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                engineLogger.severe("Error while getting move action from player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }

            if (badEndState(gameLog, player1EndState, player2EndState)) return;

            // move the players based on move decisions
            boolean validPlayer1MoveAction = true;
            boolean validPlayer2MoveAction = true;
            if (player1Decision instanceof MoveAction && player2Decision instanceof MoveAction) {
                engineLogger.debug("Both players submitted a move decision");
                gameState = GameLogic.movePlayer(gameState,
                        (MoveAction) player1Decision,
                        (MoveAction) player2Decision);
            } else if (player1Decision instanceof MoveAction) {
                engineLogger.debug("Player 2 did not submit a move decision");
                // player 2 submitted a non-move decision, so store the decision
                gameState = GameLogic.movePlayer(gameState,
                        (MoveAction) player1Decision,
                        new MoveAction(1, player1Logger, engineLogger));
                validPlayer2MoveAction = false;
            } else if (player2Decision instanceof MoveAction) {
                engineLogger.debug("Player 1 did not submit a move decision");
                // player 1 submitted a non-move decision, so store the decision
                gameState = GameLogic.movePlayer(gameState,
                        new MoveAction(0, player1Logger, engineLogger),
                        (MoveAction) player2Decision);
                validPlayer1MoveAction = false;
            } else {
                engineLogger.debug("Both players did not submit a move decision");
                validPlayer1MoveAction = false;
                validPlayer2MoveAction = false;
            }

            // send the players another game state after moving
            try {
                engineLogger.debug("Sending player 1's game state...");
                player1.sendGameState(gameState);
                engineLogger.debug("Sent player 1 a game state");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while sending game state to player 1: ", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                engineLogger.debug("Sending player 2's game state...");
                player2.sendGameState(gameState);
                engineLogger.debug("Sent player 2 a game state");
            } catch (IOException | IllegalThreadStateException e) {
                engineLogger.severe("Error while sending game state to player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }

            if (badEndState(gameLog, player1EndState, player2EndState)) return;

            // retrieve action decisions from players
            player1EndState = null;
            player2EndState = null;

            if (validPlayer1MoveAction) {
                try {
                    engineLogger.debug("Getting player 1's action decision...");
                    player1Decision = player1.getPlayerDecision();
                    engineLogger.debug("Got player 1's action decision");
                } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                    engineLogger.severe("Error while getting player decision from player 1", e);
                    player1EndState = PlayerEndState.ERROR;
                }
            } else {
                engineLogger.debug("Player 1 did not submit a move decision (probably an action decision), skipping getting decision");
            }

            if (validPlayer2MoveAction) {
                try {
                    engineLogger.debug("Getting player 2's action decision...");
                    player2Decision = player2.getPlayerDecision();
                    engineLogger.debug("Got player 2's action decision");
                } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                    engineLogger.severe("Error while getting player decision from player 2", e);
                    player2EndState = PlayerEndState.ERROR;
                }
            } else {
                engineLogger.debug("Player 2 did not submit a move decision (probably an action decision), skipping getting decision");
            }

            if (badEndState(gameLog, player1EndState, player2EndState)) return;

            // update game state
            gameState = GameLogic.updateGameState(gameState, player1Decision, player2Decision, gameConfig);
            engineLogger.debug("Updated game state");

            long endTime = System.nanoTime();
            engineLogger.info(String.format("Turn %d took %.2f milliseconds", gameState.getTurn() - 1, (endTime - startTime) / 1e6));

            player1.getLogger().incrementTurn();
            player2.getLogger().incrementTurn();
            engineLogger.incrementTurn();

        } while (!GameLogic.isGameOver(gameState, gameConfig));

        System.out.printf("Player 1: $%.2f, Player 2: $%.2f\n",
                gameState.getPlayer1().getMoney(), gameState.getPlayer2().getMoney());
        GameLogic.setWinners(gameLog, gameState, engineLogger);
        //Display achievements
        boolean p1win = gameState.getPlayer1().getMoney() > gameState.getPlayer2().getMoney();
        List<String> p1achievements = gameState.getPlayer1().getAchievements().getFinalAchievements(p1win, gameConfig.STARTING_MONEY, gameState.getPlayer1().getMoney());
        List<String> p2achievements = gameState.getPlayer2().getAchievements().getFinalAchievements(!p1win, gameConfig.STARTING_MONEY, gameState.getPlayer2().getMoney());
        engineLogger.info("Player 1 has unlocked the following achievements:");
        if (p1achievements.size() == 0) {
            engineLogger.info("Player 1 does not unlock any achievements");
        }
        for (int i = 0; i < p1achievements.size(); i++) {
            engineLogger.info(p1achievements.get(i));
        }
        engineLogger.info("Player 2 has unlocked the following achievements:");
        if (p2achievements.size() == 0) {
            engineLogger.info("Player 2 does not unlock any achievements");
        }
        for (int i = 0; i < p2achievements.size(); i++) {
            engineLogger.info(p2achievements.get(i));
        }
    }

    private static boolean badEndState(GameLog gameStates, PlayerEndState player1EndState, PlayerEndState player2EndState) {
        if (player1EndState != null || player2EndState != null) {
            if (player1EndState == PlayerEndState.ERROR && player2EndState == PlayerEndState.ERROR) {
                gameStates.setPlayer1EndState(PlayerEndState.ERROR);
                gameStates.setPlayer2EndState(PlayerEndState.ERROR);
            } else if (player1EndState == PlayerEndState.ERROR) {
                gameStates.setPlayer1EndState(PlayerEndState.ERROR);
                gameStates.setPlayer2EndState(PlayerEndState.WON);
            } else {
                gameStates.setPlayer1EndState(PlayerEndState.WON);
                gameStates.setPlayer2EndState(PlayerEndState.ERROR);
            }
            return true;
        }
        return false;
    }

    /**
     * Helper function to write a List of Strings to a file
     *
     * @param toWrite  List of Strings to write
     * @param fileName file to write to
     */
    private static void writeListToFile(List<String> toWrite, String fileName, JsonLogger engineLogger) {
        try {
            Files.write(Paths.get(fileName), toWrite, StandardCharsets.UTF_8);
        } catch (Exception e) {
            engineLogger.severe(String.format("Wasn't able to write to file (%s), writing to log instead.",
                    fileName), e);
            engineLogger.info(String.join(System.getProperty("line.separator"), toWrite));
        }
    }
}

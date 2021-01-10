package mech.mania.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;
import mech.mania.engine.model.PlayerDecisionParseException;
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

    private static final JsonLogger ENGINE_LOGGER = new JsonLogger(0);
    private static final JsonLogger PLAYER1_LOGGER = new JsonLogger(0);
    private static final JsonLogger PLAYER2_LOGGER = new JsonLogger(0);

    /**
     * The Main function. This will get the command line arguments, create the
     * players via PlayerCommunicationInfo objects, start the game using gameLoop,
     * and call writeListToFile to write the resulting log files to their respective
     * logs (player1, player2, and the game log).
     * @param args Program arguments
     */
    public static void main( String[] args ) {
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

        // should the logger print debug statements?
        PLAYER1_LOGGER.setDebug(commandLine.hasOption("d"));
        PLAYER2_LOGGER.setDebug(commandLine.hasOption("d"));
        ENGINE_LOGGER.setDebug(commandLine.hasOption("d"));

        // using the arguments from the command line, package up all necessary
        // arguments into a PlayerCommunicationInfo object
        PlayerCommunicationInfo player1 = new PlayerCommunicationInfo(
                gameConfig, ENGINE_LOGGER, PLAYER1_LOGGER,
                commandLine.getOptionValue("n"),
                commandLine.getOptionValue("e"));
        PlayerCommunicationInfo player2 = new PlayerCommunicationInfo(
                gameConfig, ENGINE_LOGGER, PLAYER2_LOGGER,
                commandLine.getOptionValue("N"),
                commandLine.getOptionValue("E"));

        PlayerEndState player1EndState = null;
        PlayerEndState player2EndState = null;

        // player process startup
        try {
            player1.start();
            player1.askForStartingItems();
        } catch (IOException | IllegalThreadStateException e) {
            ENGINE_LOGGER.severe("Player 1 failed to start", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            player2.start();
            player2.askForStartingItems();
        } catch (IOException | IllegalThreadStateException e) {
            ENGINE_LOGGER.severe("Player 2 failed to start", e);
            player2EndState = PlayerEndState.ERROR;
        }

        // start the game
        PLAYER1_LOGGER.incrementTurn();
        PLAYER2_LOGGER.incrementTurn();
        ENGINE_LOGGER.incrementTurn();

        if (player1EndState != null || player2EndState != null) {
            player1EndState = PlayerEndState.ERROR;
            player2EndState = PlayerEndState.ERROR;
        } else {
            ENGINE_LOGGER.info("Successful player initialization");

            GameLog gameLog = new GameLog();
            gameLoop(gameConfig, gameLog, player1, player2);

            player1EndState = gameLog.getPlayer1EndState();
            player2EndState = gameLog.getPlayer2EndState();

            ENGINE_LOGGER.info("Finished game loop");

            Gson serializer = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String gameLogJson = serializer.toJson(gameLog, GameLog.class);
            writeListToFile(Collections.singletonList(gameLogJson), commandLine.getOptionValue("g", gameConfig.REPLAY_FILENAME));
        }

        try {
            player1.stop();
        } catch (IOException e) {
            ENGINE_LOGGER.severe("Unable to stop player 1 (check bot logs)", e);
        }
        try {
            player2.stop();
        } catch (IOException e) {
            ENGINE_LOGGER.severe("Unable to stop player 2 (check bot logs)", e);
        }

        PLAYER1_LOGGER.incrementTurn();
        PLAYER2_LOGGER.incrementTurn();
        ENGINE_LOGGER.incrementTurn();

        // finish game by writing all log files and replay files
        PLAYER1_LOGGER.writeToFile(commandLine.getOptionValue("l", player1.getPlayerName() + gameConfig.PLAYERLOG_EXTENSION));
        PLAYER2_LOGGER.writeToFile(commandLine.getOptionValue("L", player2.getPlayerName() + gameConfig.PLAYERLOG_EXTENSION));
        ENGINE_LOGGER.writeToFile(gameConfig.ENGINELOG_FILENAME);

        System.out.println("Game complete. PLAYER1: " + player1EndState + ", PLAYER2: " + player2EndState);
    }

    /**
     * This method will initialize and parse the command line arguments
     * using com.apache.commons.cli.
     * Example program arguments (lowercase corresponds to player1, uppercase to player2)
     * <code>./engine -n "player1" -e "./bot1.py" -N "player2" -E "./bot2.py"</code>
     *
     * @param args String[] that contains the arguments to the program call
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
     * @param gameStates GameLog object that contains the running list of GameState objects. Should be empty, will be filled
     * @param player1 PlayerCommunicationInfo object that keeps information about communication with player 1
     * @param player2 PlayerCommunicationInfo object that keeps information about communication with player 2
     */
    protected static void gameLoop(Config gameConfig, GameLog gameStates,
                                   PlayerCommunicationInfo player1,
                                   PlayerCommunicationInfo player2) {
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
                player1.sendGameState(gameState);
            } catch (IOException | IllegalThreadStateException e) {
                ENGINE_LOGGER.severe("Error while sending game state to player 1: ", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                player2.sendGameState(gameState);
            } catch (IOException | IllegalThreadStateException e) {
                ENGINE_LOGGER.severe("Error while sending game state to player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }

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
                return;
            }

            // add game states to list of total game states
            gameStates.addState(new GameState(gameState));

            // retrieve decisions from players
            player1EndState = null;
            player2EndState = null;
            PlayerDecision player1Decision = null;
            PlayerDecision player2Decision = null;

            try {
                player1Decision = player1.getPlayerDecision();
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                ENGINE_LOGGER.severe("Error while getting player decision from player 1", e);
                player1EndState = PlayerEndState.ERROR;
            }

            try {
                player2Decision = player2.getPlayerDecision();
            } catch (IOException | IllegalThreadStateException | PlayerDecisionParseException e) {
                ENGINE_LOGGER.severe("Error while getting player decision from player 2", e);
                player2EndState = PlayerEndState.ERROR;
            }

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
                return;
            }

            long endTime = System.nanoTime();
            ENGINE_LOGGER.info(String.format("Turn %d took %.2f milliseconds", gameState.getTurn(), (endTime - startTime) / 1e6));

            PLAYER1_LOGGER.incrementTurn();
            PLAYER2_LOGGER.incrementTurn();
            ENGINE_LOGGER.incrementTurn();

            // update game state
            gameState = GameLogic.updateGameState(gameState, player1Decision, player2Decision);

        } while (!GameLogic.isGameOver(gameState));

        // TODO: figure out how to get winner and loser from GameLogic
        gameStates.setPlayer1EndState(player1EndState);
        gameStates.setPlayer2EndState(player2EndState);
    }

    /**
     * Helper function to write a List of Strings to a file
     *
     * @param toWrite List of Strings to write
     * @param fileName file to write to
     */
    private static void writeListToFile(List<String> toWrite, String fileName) {
        try {
            Files.write(Paths.get(fileName), toWrite, StandardCharsets.UTF_8);
        } catch (Exception e) {
            ENGINE_LOGGER.severe(String.format("Wasn't able to write to file (%s), writing to log instead.",
                    fileName), e);
            ENGINE_LOGGER.info(String.join(System.getProperty("line.separator"), toWrite));
        }
    }
}

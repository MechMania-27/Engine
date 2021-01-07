package mech.mania.engine;

import com.google.gson.Gson;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class that runs the game.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger("Main");

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
            LOGGER.severe("Config file could not be read: " + e.getMessage());
            return;
        }
        CommandLine commandLine = getCommandLineArgs(args, gameConfig);
        if (commandLine == null) {
            return;
        }

        // using the arguments from the command line, package up all necessary
        // arguments into a PlayerCommunicationInfo object
        PlayerCommunicationInfo player1 = new PlayerCommunicationInfo(
                gameConfig,
                commandLine.getOptionValue("n"),
                commandLine.getOptionValue("e"));
        PlayerCommunicationInfo player2 = new PlayerCommunicationInfo(
                gameConfig,
                commandLine.getOptionValue("N"),
                commandLine.getOptionValue("E"));

        PlayerEndState player1EndState = null;
        PlayerEndState player2EndState = null;

        // player process startup
        try {
            player1.start();
            player1.askForStartingItems();
        } catch (IOException e) {
            LOGGER.warning("Player 1 failed to start: " + e.getMessage());
            player1EndState = PlayerEndState.ERROR;
        } catch (IllegalThreadStateException e) {
            // player timed out
            LOGGER.warning("Player 1 failed to start: " + e.getMessage());
            player1EndState = PlayerEndState.TIMED_OUT;
        }

        try {
            player2.start();
            player2.askForStartingItems();
        } catch (IOException e) {
            LOGGER.warning("Player 2 failed to start: " + e.getMessage());
            player2EndState = PlayerEndState.ERROR;
        } catch (IllegalThreadStateException e) {
            // player timed out
            LOGGER.warning("Player 2 failed to start: " + e.getMessage());
            player2EndState = PlayerEndState.TIMED_OUT;
        }

        if (player1EndState != null || player2EndState != null) {
            // TODO: decide on how this is handled
        } else {
            LOGGER.fine("Successful player initialization");

            GameLog gameLog = new GameLog();
            gameLoop(gameConfig, gameLog, player1, player2);
            player1EndState = gameLog.getPlayer1EndState();
            player2EndState = gameLog.getPlayer2EndState();

            LOGGER.fine("Finished game loop");

            // finish game by writing all log files and replay files
            writeListToFile(player1.getLogs(), commandLine.getOptionValue("l", player1.getPlayerName() + ".log"));
            writeListToFile(player2.getLogs(), commandLine.getOptionValue("L", player2.getPlayerName() + ".log"));

            String gameLogJson = new Gson().toJson(gameLog, GameLog.class);
            writeListToFile(Collections.singletonList(gameLogJson), commandLine.getOptionValue("g", gameConfig.REPLAY_FILENAME));

            LOGGER.fine("Finished game log write");

            try {
                player1.stop();
            } catch (IOException e) {
                LOGGER.warning("Unable to stop player 1 (check bot logs): " + e.getMessage());
            }
            try {
                player2.stop();
            } catch (IOException e) {
                LOGGER.warning("Unable to stop player 2 (check bot logs): " + e.getMessage());
            }
        }

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

        // TODO: add option for debug/verbose for LOGGER.fine() calls
        // TODO: add option for no log files generated?

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
            LOGGER.warning("Failed to parse command line arguments: " + e.getMessage());
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
            // send game states
            player1EndState = null;
            player2EndState = null;

            try {
                player1.sendGameState(gameState);
            } catch (IOException e) {
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while sending game state to player 1: " + e.getMessage());
                player1EndState = PlayerEndState.ERROR;
            } catch (IllegalThreadStateException e) {
                // player timed out
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while sending game state to player 1: " + e.getMessage());
                player1EndState = PlayerEndState.TIMED_OUT;
            }

            try {
                player2.sendGameState(gameState);
            } catch (IOException e) {
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while sending game state to player 2: " + e.getMessage());
                player2EndState = PlayerEndState.ERROR;
            } catch (IllegalThreadStateException e) {
                // player timed out
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while sending game state to player 2: " + e.getMessage());
                player2EndState = PlayerEndState.TIMED_OUT;
            }

            if (player1EndState != null || player2EndState != null) {
                // TODO: figure this logic out
                gameStates.setPlayer1EndState(player1EndState);
                gameStates.setPlayer2EndState(player2EndState);
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
            } catch (IOException e) {
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while getting player decision from player 1: " + e.getMessage());
                player1EndState = PlayerEndState.ERROR;
            } catch (IllegalThreadStateException e) {
                // player timed out
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while getting player decision from player 1: " + e.getMessage());
                player1EndState = PlayerEndState.TIMED_OUT;
            }

            try {
                player2Decision = player2.getPlayerDecision();
            } catch (IOException e) {
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while getting player decision from player 2: " + e.getMessage());
                player2EndState = PlayerEndState.ERROR;
            } catch (IllegalThreadStateException e) {
                // player timed out
                LOGGER.warning("[Turn " + gameState.getTurn() + "] Error while getting player decision from player 2: " + e.getMessage());
                player2EndState = PlayerEndState.TIMED_OUT;
            }

            if (player1EndState != null || player2EndState != null) {
                // TODO: figure this logic out
                gameStates.setPlayer1EndState(player1EndState);
                gameStates.setPlayer2EndState(player2EndState);
                return;
            }

            // update game state
            gameState = GameLogic.updateGameState(gameState, player1Decision, player2Decision);
        } while (!GameLogic.isGameOver(gameState));

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
            LOGGER.warning(String.format("Wasn't able to write to file (%s), writing to stdout instead. Error: %s",
                    fileName, e.getMessage()));
            LOGGER.info(String.join(System.getProperty("line.separator"), toWrite));
        }
    }
}

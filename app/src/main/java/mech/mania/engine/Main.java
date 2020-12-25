package mech.mania.engine;

import com.google.gson.Gson;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.core.Winner;
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
import java.util.logging.*;

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

        Config gameConfig = new Config();
        CommandLine commandLine = getCommandLineArgs(args, gameConfig);
        if (commandLine == null) {
            return;
        }

        // using the arguments from the command line, package up all necessary
        // arguments into a PlayerCommunicationInfo object
        PlayerCommunicationInfo player1 = new PlayerCommunicationInfo(
                commandLine.getOptionValue("n"),
                commandLine.getOptionValue("e"));
        PlayerCommunicationInfo player2 = new PlayerCommunicationInfo(
                commandLine.getOptionValue("N"),
                commandLine.getOptionValue("E"));

        // player process startup
        if (!player1.start()) {
            // player 1 failed to start
            LOGGER.warning("Player 1 failed to start. Aborting. See stderr for details.");
            return;
        }
        if (!player2.start()) {
            // player 2 failed to start
            LOGGER.warning("Player 2 failed to start. Aborting. See stderr for details.");
            return;
        }

        // persistent object that will keep track of all game states that will
        // be outputted each round.
        GameLog gameStates = new GameLog();
        Winner winner = gameLoop(gameConfig, gameStates, player1, player2);

        LOGGER.info("after game loop");

        // finish game by writing all log files and replay files
        writeListToFile(player1.getLog(), commandLine.getOptionValue("l", player1.getPlayerName() + ".log"));
        writeListToFile(player2.getLog(), commandLine.getOptionValue("L", player2.getPlayerName() + ".log"));
        // LOGGER.info("after player write");
        String gameLogJson = new Gson().toJson(gameStates, GameLog.class);
        writeListToFile(Collections.singletonList(gameLogJson), commandLine.getOptionValue("g", gameConfig.getDefaultReplayFileName()));

        LOGGER.info("after game log write");
        try {
            player1.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player2.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Game complete. Winner: " + winner);
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
    public static CommandLine getCommandLineArgs(String[] args, Config gameDefaults) {
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

        // ============================ GAME LOG FILENAMES ========================
        Option replayFileName = Option.builder("r")
                .longOpt("replayfile-name")
                .hasArg()
                .argName("filename")
                .desc("Name of the replay file to be made (for visualizer) " +
                        "(default=" + gameDefaults.getDefaultReplayFileName() + ")")
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
     * TODO: change the exit condition
     *
     * @param gameConfig Config object that contains game default values
     * @param gameStates GameLog object that contains the running list of GameState objects
     * @param player1 PlayerCommunicationInfo object that keeps information about communication with player 1
     * @param player2 PlayerCommunicationInfo object that keeps information about communication with player 2
     * @return the winner as a mech.mania.engine.core.Winner enum
     */
    private static Winner gameLoop(Config gameConfig, GameLog gameStates, PlayerCommunicationInfo player1, PlayerCommunicationInfo player2) {
        GameState gameState = new GameState(gameConfig,
                player1.getPlayerName(), player2.getPlayerName());

        int turn = 1;
        do {
            // LOGGER.info(String.format("Turn %d", turn));
            // send game states
            try {
                player1.sendGameState(gameState);
            } catch (Exception e) {
                LOGGER.warning("Exception while sending player 1 game state: " + e.getMessage());
                return Winner.PLAYER2;
            }

            try {
                player2.sendGameState(gameState);
            } catch (Exception e) {
                LOGGER.warning("Exception while sending player 2 game state: " + e.getMessage());
                return Winner.PLAYER1;
            }

            // add game states to list of total game states
            gameStates.addState(new GameState(gameState));

            // retrieve decisions from players
            PlayerDecision player1Decision;
            try {
                player1Decision = player1.getPlayerDecision();
            } catch (Exception e) {
                LOGGER.warning("Exception while getting player 1 decision: " + e.getMessage());
                return Winner.PLAYER2;
            }

            PlayerDecision player2Decision;
            try {
                player2Decision = player2.getPlayerDecision();
            } catch (Exception e) {
                LOGGER.warning("Exception while getting player 2 decision: " + e.getMessage());
                return Winner.PLAYER1;
            }

            // update game state
            gameState = GameLogic.updateGameState(gameState, player1Decision, player2Decision);
            turn++;

            if (turn == 100) {
                break;
            }
        } while (!GameLogic.isGameOver(gameState));

        return GameLogic.getWinner(gameState);
    }

    /**
     * Helper function to write a List of Strings to a file
     *
     * @param toWrite List of Strings to write
     * @param fileName file to write to
     */
    public static void writeListToFile(List<String> toWrite, String fileName) {
        if (fileName == null || fileName.length() == 0) {
            LOGGER.warning("File name is empty. Aborting write");
            return;
        }

        try {
            Files.write(Paths.get(fileName), toWrite, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warning(String.format("Wasn't able to write to file (%s), writing to stdout instead. Error: %s",
                    fileName, e.getMessage()));
            LOGGER.info(String.join(System.getProperty("line.separator"), toWrite));
        }
    }
}

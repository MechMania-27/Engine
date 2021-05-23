package mech.mania.engine;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.Game;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
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

    private static final Gson gameLogSerializer = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equals("playerNum");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

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

        JsonLogger player1Logger = new JsonLogger(0);
        JsonLogger player2Logger = new JsonLogger(0);
        JsonLogger engineLogger = new JsonLogger(0);

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
            player1.start();
            // engineLogger.debug("Started player 1 process");
            player1.askForStartingItems();
            // engineLogger.debug("Finished asking player 1 for starting items");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Player 1 failed to start", e);
            player1EndState = PlayerEndState.ERROR;
        }

        try {
            player2.start();
            // engineLogger.debug("Started player 2 process");
            player2.askForStartingItems();
            // engineLogger.debug("Finished asking player 2 for starting items");
        } catch (IOException | IllegalThreadStateException e) {
            engineLogger.severe("Player 2 failed to start", e);
            player2EndState = PlayerEndState.ERROR;
        }

        // start the game
        player1Logger.incrementTurn();
        player2Logger.incrementTurn();
        engineLogger.incrementTurn();

        if (player1EndState != null || player2EndState != null) {
            player1EndState = PlayerEndState.ERROR;
            player2EndState = PlayerEndState.ERROR;
        } else {
            engineLogger.info("Successful player initialization");

            Game game = new Game(gameConfig, player1, player2, engineLogger);
            GameLog gameLog = game.run();

            player1EndState = gameLog.getPlayer1EndState();
            player2EndState = gameLog.getPlayer2EndState();

            engineLogger.info("Finished game loop");

            String gameLogJson = gameLogSerializer.toJson(gameLog, GameLog.class);
            writeListToFile(Collections.singletonList(gameLogJson),
                    commandLine.getOptionValue("g", gameConfig.REPLAY_FILENAME),
                    engineLogger);
        }

        player1.stop();
        // engineLogger.debug("Stopped player 1");
        player2.stop();
        // engineLogger.debug("Stopped player 2");

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
     * Helper function to write a List of Strings to a file
     *
     * @param toWrite List of Strings to write
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
}

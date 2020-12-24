package mech.mania.engine;

import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.model.GameState;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import mech.mania.engine.model.PlayerDecision;
import org.apache.commons.cli.*;

/**
 * Class that runs the game.
 */
public class Main {
    public static void main( String[] args ) {
        CommandLine commandLine;
        try {
            commandLine = getCommandLineArgs(args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        }

        // using the information from the command line, package up all necessary
        // information into a PlayerCommunicationInfo object
        PlayerCommunicationInfo player1 = new PlayerCommunicationInfo(
                commandLine.getOptionValue("n"),
                commandLine.getOptionValue("e"));
        PlayerCommunicationInfo player2 = new PlayerCommunicationInfo(
                commandLine.getOptionValue("N"),
                commandLine.getOptionValue("E"));

        // TODO: handle startup errors
        player1.start();
        player2.start();

        // persistent object that will keep track of all game states that will
        // be outputted each round.
        GameLog gameStates = new GameLog();

        // use the getConfig function to initialize the Config object for game
        // parameters
        Config gameConfig = Config.getConfig();
        GameState gameState = new GameState(gameConfig,
                player1.getPlayerName(), player2.getPlayerName());

        do {
            // TODO: handle errors gracefully
            player1.sendGameState(gameState);
            player2.sendGameState(gameState);

            assert gameState != null;
            gameStates.addState(new GameState(gameState));

            PlayerDecision player1Decision = player1.getPlayerDecision();
            PlayerDecision player2Decision = player2.getPlayerDecision();

            gameState = GameLogic.updateGameState(player1Decision, player2Decision);
        } while (!GameLogic.isGameOver(gameState));

        // TODO: add command line arguments to determine whether game log should be written to file and specify filename
        player1.writeLogToFile();
        player2.writeLogToFile();
        writeLogToFile(gameStates, "game.log");
    }

    /**
     * This method will initialize and parse the command line arguments
     * using com.apache.commons.cli.
     * Example program arguments (lowercase corresponds to player1, uppercase to player2)
     * <code>./engine -n "player1" -e "./bot1.py" -N "player2" -E "./bot2.py"</code>
     *
     * @param args String[] that contains the arguments to the program call
     * @return a com.apache.commons.cli.CommandLine object that can be used to read parsed values
     * @throws ParseException if the command line arguments are not parsed correctly
     */
    public static CommandLine getCommandLineArgs(String[] args) throws ParseException {
        Options options = new Options();

        Option player1Name = Option.builder("n")
                .longOpt("player1-name")
                .argName("name")
                .required()
                .desc("First player's name")
                .build();
        options.addOption(player1Name);
        Option player1Executable = Option.builder("e")
                .longOpt("player1-executable")
                .argName("command")
                .required()
                .desc("First player's executable as a string")
                .build();
        options.addOption(player1Executable);

        Option player2Name = Option.builder("N")
                .longOpt("player2-name")
                .argName("name")
                .required()
                .desc("Second player's name")
                .build();
        options.addOption(player2Name);
        Option player2Executable = Option.builder("E")
                .longOpt("player2-executable")
                .argName("command")
                .required()
                .desc("Second player's executable as a string")
                .build();
        options.addOption(player2Executable);

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }

    /**
     * Will write the given GameLog object to the file fileName using GSON serialization
     *
     * @param log the GameLog object to output
     * @param fileName file to write to
     */
    public static void writeLogToFile(GameLog log, String fileName) {
        // write to log file using JSON serialization
    }


}

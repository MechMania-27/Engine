package mech.mania.engine.networking;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.decisions.PlayerDecision;
import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import mech.mania.engine.util.MainUtils;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.util.PlayerParseUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * A class that will hold information about communication with the player, including
 * the player's name and any pipes open to communicate with the player.
 */
public class PlayerCommunicationInfo {
    private final JsonLogger logger;
    private final JsonLogger engineLogger;

    private final Config gameConfig;

    private final int playerNum;
    private final String playerName;
    private final String[] playerExecutable;
    private Process process;
    private SafeBufferedReader inputReader;
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private BufferedWriter writer;

    private ItemType startingItem;
    private UpgradeType startingUpgradeType;

    public PlayerCommunicationInfo(Config gameConfig, JsonLogger engineLogger, JsonLogger logger,
                                   int playerNum, String playerName, String playerExecutable) {
        this.engineLogger = engineLogger;
        this.logger = logger;
        this.gameConfig = gameConfig;
        this.playerNum = playerNum;
        this.playerName = playerName;
        this.playerExecutable = MainUtils.translateCommandline(playerExecutable);
    }

    /**
     * Function that should be called after the constructor to start the bot's process.
     *
     * @throws IOException if the process fails to start
     */
    public void start() throws IOException {
        // use executable string to start process, initialize streams to communicate
        ProcessBuilder pb = new ProcessBuilder(playerExecutable);
        process = pb.start();

        // be constantly catching the error stream to keep the buffer clear
        // whenever we are reading the inputstream as well as for properly
        // completely reading the errorstream
        // (reading from both input and error streams requires at least one
        // extra thread reading one of the streams to keep the buffer empty
        // https://stackoverflow.com/questions/1349298/reading-input-and-error-streams-concurrently-using-bufferedreaders-hangs)
        Thread errorStreamCatchProcess = new Thread(() -> {
            try {
                IOUtils.copy(process.getErrorStream(), errorStream);
            } catch (IOException e) {
                engineLogger.severe("Failed to read from bot error stream", e);
            }
        });
        errorStreamCatchProcess.start();

        inputReader = new SafeBufferedReader(new InputStreamReader(process.getInputStream()), gameConfig.PLAYER_TIMEOUT);
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        engineLogger.debug(String.format("Bot (pid %d): started", MainUtils.tryGetPid(process)));
    }

    /**
     * Stops the player's process. Will cause subsequent calls to getPlayerDecision and sendGameState to fail
     */
    public void stop() {
        try {
            inputReader.close();
            writer.close();
        } catch (IOException e) {
            engineLogger.debug(String.format("Bot (pid %d): closed with error (%s): %s",
                    MainUtils.tryGetPid(process), e.getClass(), e.getMessage()));
            return;
        }
        engineLogger.debug(String.format("Bot (pid %d): closed without error", MainUtils.tryGetPid(process)));
    }

    /**
     * Gets the player's decision using the inputReader
     *
     * @return PlayerDecision object containing all of the information about the player's decision
     * @throws IOException if there is an error in communicating with the player
     * @throws IllegalThreadStateException if the player times out while sending decision
     * @throws PlayerDecisionParseException if the engine fails to parse the player's decision
     */
    public PlayerDecision getPlayerDecision() throws IOException, IllegalThreadStateException, PlayerDecisionParseException {
        String response;
        try {
            response = inputReader.readLine();
        } finally {
            String[] allMessages = errorStream.toString().split("\n");
            for (String message : allMessages) {
                if (!message.contains(":")) {
                    logger.severe(message);
                    continue;
                }
                switch (message.substring(0, message.indexOf(":"))) {
                    case "info":
                        logger.info(message.substring(message.indexOf(":") + 2));
                        break;
                    case "debug":
                        logger.debug(message.substring(message.indexOf(":") + 2));
                        break;
                    default:
                        logger.severe(message);
                }
            }
            errorStream.reset();
        }

        engineLogger.debug(String.format("Bot (pid %d): reading: %.30s",
                MainUtils.tryGetPid(process), response));

        try {
            return PlayerParseUtils.parseDecision(playerNum, response);
        } catch (PlayerDecisionParseException e){
            throw(e);
        }
    }

    /**
     * Sends a <code>GameState</code> object to the player's standard in
     *
     * @param gameState GameState object to send
     * @throws IOException if there is an error while connecting to the player
     */
    public void sendGameState(GameState gameState) throws IOException {
        // send player turn to stdin
        String message = PlayerParseUtils.sendInfoFromGameState(gameState);
        engineLogger.debug(String.format("Bot (pid %d): writing: %.30s",
                MainUtils.tryGetPid(process), message));
        writer.append(message);
        writer.newLine();
        writer.flush();
    }

    /**
     * Method that should be called after start but before the game starts to ask
     * for Item and Upgrade that will be used.
     */
    public void askForStartingItems() throws IOException, IllegalThreadStateException {
        String itemResponse = inputReader.readLine();
        this.startingItem = PlayerParseUtils.itemFromString(itemResponse);
        String upgradeResponse = inputReader.readLine();
        this.startingUpgradeType = PlayerParseUtils.upgradeFromString(upgradeResponse);
    }

    public String getPlayerName() {
        return playerName;
    }

    public ItemType getStartingItem() {
        return startingItem;
    }

    public UpgradeType getStartingUpgrade() {
        return startingUpgradeType;
    }

    public JsonLogger getLogger() {
        return logger;
    }

}

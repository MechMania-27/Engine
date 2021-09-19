package mech.mania.engine.networking;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.ItemType;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.UpgradeType;
import mech.mania.engine.model.decisions.PlayerDecision;
import mech.mania.engine.util.MainUtils;
import mech.mania.engine.util.PlayerCommunicationUtils;

import java.io.*;
import java.util.concurrent.TimeUnit;

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
    private BufferedReader errorStream;
//    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private BufferedWriter writer;

    private Thread errorStreamCatchProcess;
    private Thread errorStreamReadProcess;

    private ItemType startingItem;
    private UpgradeType startingUpgradeType;
    
    private int pid;

    private boolean gameOver = false;

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
        try {
            process = pb.start();
            engineLogger.debug("Waiting for process to come alive");
            while (!process.isAlive()) {
                Thread.sleep(10);
            }
            engineLogger.debug("Process is alive");
            pid = MainUtils.tryGetPid(process);
        } catch (Exception e) {
            engineLogger.severe("Failed to start process for bot", e);
        }

        // originally set timeout to heartbeat timeout. once we receive a heartbeat from the bot we can set it to the
        // player turn timeout
        inputReader = new SafeBufferedReader(new InputStreamReader(process.getInputStream()), gameConfig.HEARTBEAT_TIMEOUT);
        errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        errorStreamReadProcess = new Thread(() -> {
            while (!gameOver) {
                String allMessagesStr;
                try {
                    allMessagesStr = errorStream.readLine();
                } catch (IOException e) {
                    continue;
                }

                if (allMessagesStr == null) {
                    continue;
                }

                for (String message : allMessagesStr.split("\n")) {
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
            }
        });
        errorStreamReadProcess.start();
    }

    /**
     * Stops the player's process. Will cause subsequent calls to getPlayerDecision and sendGameState to fail
     */
    public void stop() {
        try {
            inputReader.close();
            writer.close();
            process.destroyForcibly();
            errorStreamReadProcess.interrupt();
            gameOver = true;
        } catch (IOException e) {
            engineLogger.debug(String.format("Bot (pid %d): closed with error (%s): %s",
                    pid, e.getClass(), e.getMessage()));
            return;
        }
        engineLogger.debug(String.format("Bot (pid %d): closed without error", pid));
    }

    private String safeGetLine() throws IOException {
        String response;
        try {
            response = inputReader.readLine();
            engineLogger.debug("Received \"" + response + "\"");
        } catch (Exception e) {
            engineLogger.debug(String.format("Bot (pid %d): failed to read from (%s): %s",
                    pid, e.getClass(), e.getMessage()));

            throw(e);
        }

        return response;
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
        String response = safeGetLine();

        while (response.startsWith(" ")) {
            response = inputReader.readLine();
            logger.debug(response);
        }

        engineLogger.debug(String.format("Bot (pid %d): reading (len:%d): %.30s",
                pid, response.length(), response));

        try {
            return PlayerCommunicationUtils.parseDecision(playerNum, response, logger, engineLogger);
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
        String message = PlayerCommunicationUtils.sendInfoFromGameState(gameState, playerNum, logger.getFeedback());
        engineLogger.debug(String.format("Bot (pid %d): writing (len:%d): %.30s",
                pid, message.length(), message));
        writer.append(message);
        writer.newLine();
        writer.flush();
    }

    /**
     * Method that should be called after start but before the game starts to ask
     * for Item and Upgrade that will be used.
     */
    public void askForStartingItems() throws IOException, IllegalThreadStateException {
        String itemResponse = safeGetLine();
        while (itemResponse.startsWith(" ")) {
            itemResponse = inputReader.readLine();
            logger.debug(itemResponse);
        }
        this.startingItem = PlayerCommunicationUtils.itemFromString("NONE");

        String upgradeResponse = safeGetLine();
        while (upgradeResponse.startsWith(" ")) {
            upgradeResponse = inputReader.readLine();
            logger.debug(upgradeResponse);
        }

        this.startingUpgradeType = PlayerCommunicationUtils.upgradeFromString("NONE");
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

    public void checkHeartbeat() throws IOException {
        String response = safeGetLine();
        if (!response.equals("heartbeat")) {
            throw(new IOException("Response was not 'heartbeat'"));
        }
        inputReader.setTimeout(gameConfig.PLAYER_TIMEOUT, TimeUnit.MILLISECONDS);
        engineLogger.debug(String.format("Bot (pid %d): started process (received heartbeat)", pid));
    }
}

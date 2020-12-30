package mech.mania.engine.networking;

import com.google.gson.JsonParseException;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Item;
import mech.mania.engine.model.PlayerDecision;
import mech.mania.engine.model.Upgrade;
import mech.mania.engine.util.PlayerParseUtils;
import mech.mania.engine.util.MainUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class that will hold information about communication with the player, including
 * the player's name and any pipes open to communicate with the player.
 */
public class PlayerCommunicationInfo {
    private static final Logger LOGGER = Logger.getLogger("PlayerCommunicationInfo");

    private String playerName;
    private String[] playerExecutable;
    private Process process;
    private BufferedReader inputReader;
    private ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private BufferedWriter writer;

    private Item startingItem;
    private Upgrade startingUpgrade;

    public PlayerCommunicationInfo(String playerName, String playerExecutable) {
        this.playerName = playerName;
        this.playerExecutable = MainUtils.translateCommandline(playerExecutable);
    }

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
        new Thread(() -> {
            try {
                IOUtils.copy(process.getErrorStream(), errorStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // since inputstream will be needed every turn and will need to be blocking, these
        // other two will be started on the main thread
        LOGGER.fine(String.format("Bot (pid %d): creating", MainUtils.tryGetPid(process)));
        inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    }

    public void stop() throws IOException {
        LOGGER.fine(String.format("Bot (pid %d): closing", MainUtils.tryGetPid(process)));
        inputReader.close();
        writer.close();
    }

    public PlayerDecision getPlayerDecision() throws JsonParseException, IOException {
        // capture any stderr in log
        // capture all stdout as PlayerDecision
        String response = inputReader.readLine();
        LOGGER.fine(String.format("Bot (pid %d): reading: %s", MainUtils.tryGetPid(process), response));
        return PlayerParseUtils.decisionFromString(response);
    }

    public void sendGameState(GameState gameState) throws IOException {
        // send player turn to stdin
        String message = PlayerParseUtils.sendInfoFromGameState(gameState);
        LOGGER.fine(String.format("Bot (pid %d): writing: %s", MainUtils.tryGetPid(process), message));
        writer.append(message).append(System.getProperty("line.separator"));
        writer.flush();
    }

    /**
     * Function to return the stderr of the process that was run, useful for debugging
     * and contains any debug messages that the player may have sent.
     * @return List of Strings containing one line of the logs each
     */
    public List<String> getLogs() {
        String stringLog = new String(errorStream.toByteArray());

        // collect everything from player's stderr
        return Arrays.asList(stringLog.split("\n"));
    }

    /**
     * Method that should be called after start but before the game starts to ask
     * for Item and Upgrade that will be used.
     */
    public void askForStartingItems() throws IOException {
        String itemResponse = inputReader.readLine();
        this.startingItem = PlayerParseUtils.itemFromString(itemResponse);
        String upgradeResponse = inputReader.readLine();
        this.startingUpgrade = PlayerParseUtils.upgradeFromString(upgradeResponse);
    }

    public String getPlayerName() {
        return playerName;
    }

    public Item getStartingItem() {
        return startingItem;
    }

    public Upgrade getStartingUpgrade() {
        return startingUpgrade;
    }

}

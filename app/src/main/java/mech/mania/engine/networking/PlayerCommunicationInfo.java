package mech.mania.engine.networking;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Field;
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
    private ByteArrayOutputStream byteArrayOutputStreamBaos = new ByteArrayOutputStream();
    private BufferedWriter writer;

    public PlayerCommunicationInfo(String playerName, String playerExecutable) {
        this.playerName = playerName;
        this.playerExecutable = playerExecutable.split(" ");
    }

    public boolean start() {
        // use executable string to start process, initialize streams to communicate
        ProcessBuilder pb = new ProcessBuilder(playerExecutable);
        try {
            process = pb.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

        // be constantly catching the error stream to keep the buffer clear
        // whenever we are reading the inputstream as well as for properly
        // completely reading the errorstream
        // (reading from both input and error streams requires at least one
        // extra thread reading one of the streams to keep the buffer empty
        // https://stackoverflow.com/questions/1349298/reading-input-and-error-streams-concurrently-using-bufferedreaders-hangs)
        new Thread(() -> {
            try {
                IOUtils.copy(process.getErrorStream(), byteArrayOutputStreamBaos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // since inputstream will be needed every turn and will need to be blocking, these
        // other two will be started on the main thread
        LOGGER.fine(String.format("Bot (pid %d): creating", tryGetPid(process)));
        inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        return true;
    }

    public void stop() throws IOException {
        LOGGER.fine(String.format("Bot (pid %d): closing", tryGetPid(process)));
        inputReader.close();
        writer.close();
    }

    public PlayerDecision getPlayerDecision() throws JsonParseException, IOException {
        // capture any stderr in log
        // capture all stdout as PlayerDecision
        String response = inputReader.readLine();
        LOGGER.fine(String.format("Bot (pid %d): reading: %s", tryGetPid(process), response));
        return new Gson().fromJson(response, PlayerDecision.class);
    }

    public void sendGameState(GameState gameState) throws IOException {
        // send player turn to stdin
        String message = new Gson().toJson(gameState, GameState.class);
        LOGGER.fine(String.format("Bot (pid %d): writing: %s", tryGetPid(process), message));
        writer.append(message).append(System.getProperty("line.separator"));
        writer.flush();
    }

    /**
     * Function to return the stderr of the process that was run, useful for debugging
     * and contains any debug messages that the player may have sent.
     * @return List of Strings containing one line of the logs each
     */
    public List<String> getLogs() {
        String stringLog = new String(byteArrayOutputStreamBaos.toByteArray());

        // collect everything from player's stderr
        return Arrays.asList(stringLog.split("\n"));
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     * Helper function to get process ID of a Process object in Java. Only works
     * on UNIX computers due to syscall limitations
     *
     * https://stackoverflow.com/questions/4750470/how-to-get-pid-of-process-ive-just-started-within-java-program
     * @param process a java Process from ProcessBuilder
     * @return process id
     */
    private int tryGetPid(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return f.getInt(process);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                System.out.println("Caught exception");
            }
        }

        return 0;
    }
}

package mech.mania.engine.networking;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecision;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class that will hold information about communication with the player, including
 * the player's name and any pipes open to communicate with the player.
 */
public class PlayerCommunicationInfo {
    private static final Logger LOGGER = Logger.getLogger("PlayerCommunicationInfo");

    private String playerName;
    private String playerExecutable;
    private Process process;
    private BufferedReader inputReader;
    private ByteArrayOutputStream byteArrayOutputStreamBaos = new ByteArrayOutputStream();
    private BufferedWriter writer;
    private List<String> log = new ArrayList<>();

    public PlayerCommunicationInfo(String playerName, String playerExecutable) {
        this.playerName = playerName;
        this.playerExecutable = playerExecutable;
    }

    public boolean start() {
        // use executable string to start process, initialize streams to communicate
        ProcessBuilder pb = new ProcessBuilder(playerExecutable.split(" "));
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
        inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        return true;
    }

    public void stop() throws IOException {
        inputReader.close();
        writer.close();
    }

    public PlayerDecision getPlayerDecision() throws JsonParseException, IOException {
        // capture any stderr in log
        // capture all stdout as PlayerDecision
        String response = inputReader.readLine();
        return new Gson().fromJson(response, PlayerDecision.class);
    }

    public void sendGameState(GameState gameState) throws IOException {
        // send player turn to stdin
        String message = new Gson().toJson(gameState, GameState.class);
        writer.append(message).append(System.getProperty("line.separator"));
        writer.flush();
    }

    public List<String> getLog() {
        String stringLog = new String(byteArrayOutputStreamBaos.toByteArray());

        // collect everything from player's stderr
        log.add(stringLog);
        return log;
    }

    public String getPlayerName() {
        return playerName;
    }
}

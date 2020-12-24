package mech.mania.engine.model;

public class PlayerCommunicationInfo {

    private String playerName;
    private String playerExecutable;
    private String log;

    public PlayerCommunicationInfo(String playerName, String playerExecutable) {
        this.playerName = playerName;
        this.playerExecutable = playerExecutable;
    }

    public void start() {
        // use executable string to start process, initialize streams to communicate
    }

    public PlayerDecision getPlayerDecision() {
        // capture any stderr in log
        // capture all stdout as PlayerDecision
        return null;
    }

    public void sendGameState(GameState gameState) {
        // send player turn to stdin
    }

    public void addToLogFile(String log) {
        this.log += log;
    }

    public void writeLogToFile(String fileName) {
        // write log string to file
    }

    public void writeLogToFile() {
        writeLogToFile(playerName);
    }

    public String getPlayerName() {
        return playerName;
    }
}

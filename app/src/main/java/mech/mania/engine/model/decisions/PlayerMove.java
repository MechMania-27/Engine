package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;

public class PlayerMove {
    private final String playerID;
    private final int destX;
    private final int destY;

    public PlayerMove(String playerID, int destX, int destY) {
        this.playerID = playerID;
        this.destX = destX;
        this.destY = destY;
    }

    public PlayerMove(String playerID, String input) {
        this.playerID = playerID;
        String[] words = input.split(" ");
        if (words.length < 2) {
            throw new IllegalArgumentException("Player ID, destination X, and destination Y all need to be specified");
        }
        destX = Integer.parseInt(words[0]);
        destY = Integer.parseInt(words[1]);
    }

    public void moveToLocation(GameState state) {
        // stub for now
    }
}

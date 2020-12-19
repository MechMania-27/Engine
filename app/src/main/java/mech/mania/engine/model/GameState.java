package mech.mania.engine.model;

import java.util.List;

public class GameState {
    private List<Position> playerPos;

    public GameState(List<Position> playerPos) {
        this.playerPos = playerPos;
    }

    public List<Position> getPlayerPos() {
        return playerPos;
    }

    public void setPlayerPos(List<Position> playerPos) {
        this.playerPos = playerPos;
    }
}

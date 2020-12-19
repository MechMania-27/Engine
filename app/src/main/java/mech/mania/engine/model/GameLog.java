package mech.mania.engine.model;

import java.util.List;

public class GameLog {
    private TileMap tileMap;
    private List<String> playerNames;
    private List<Position> initPlayersPos;
    private List<GameState> states;

    public GameLog(TileMap tileMap, List<String> playerNames, List<Position> initPlayersPos, List<GameState> states) {
        this.tileMap = tileMap;
        this.playerNames = playerNames;
        this.initPlayersPos = initPlayersPos;
        this.states = states;
    }

    public List<GameState> getStates() {
        return states;
    }

    public void setStates(List<GameState> states) {
        this.states = states;
    }

    public List<Position> getInitPlayersPos() {
        return initPlayersPos;
    }

    public void setInitPlayersPos(List<Position> initPlayersPos) {
        this.initPlayersPos = initPlayersPos;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }
}

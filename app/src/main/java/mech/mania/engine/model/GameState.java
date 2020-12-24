package mech.mania.engine.model;

import java.util.List;

public class GameState {
    private List<Player> players;
    private TileMap tileMap;

    public GameState(List<Player> players, TileMap tileMap) {
        this.players = players;
        this.tileMap = tileMap;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

}

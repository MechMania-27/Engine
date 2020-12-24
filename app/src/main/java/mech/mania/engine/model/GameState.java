package mech.mania.engine.model;

import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<Player> players;
    private TileMap tileMap;

    public GameState(Config gameConfig, String player1Name, String player2Name) {
        players = new ArrayList<>();
        players.add(new Player(player1Name));
        players.add(new Player(player2Name));
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

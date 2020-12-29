package mech.mania.engine.model;

import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<Player> players;
    private TileMap tileMap;

    public GameState(Config gameConfig,
                     String player1Name, Item player1Item, Upgrade player1Upgrade,
                     String player2Name, Item player2Item, Upgrade player2Upgrade) {
        players = new ArrayList<>();

        int startingMoney = gameConfig.getStartingMoney();
        Position player1Position = new Position(0, 0);
        Position player2Position = new Position(gameConfig.getBoardWidth(), 0);

        Player player1 = new Player(player1Name, player1Position, player1Item, player1Upgrade, startingMoney);
        Player player2 = new Player(player2Name, player2Position, player2Item, player2Upgrade, startingMoney);

        tileMap = new TileMap(gameConfig, player1, player2);
    }

    public GameState(GameState other) {
        this.players = other.getPlayers();
        this.tileMap = other.getTileMap();
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

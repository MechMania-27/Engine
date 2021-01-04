package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    @Expose
    private List<Player> players;
    @Expose
    private TileMap tileMap;

    public GameState(Config gameConfig,
                     String player1Name, ItemType player1Item, UpgradeType player1UpgradeType,
                     String player2Name, ItemType player2Item, UpgradeType player2UpgradeType) {
        int startingMoney = gameConfig.STARTING_MONEY;
        Position player1Position = new Position(0, 0);
        Position player2Position = new Position(gameConfig.BOARD_WIDTH - 1, 0);

        Player player1 = new Player(player1Name, player1Position, player1Item, player1UpgradeType, startingMoney);
        Player player2 = new Player(player2Name, player2Position, player2Item, player2UpgradeType, startingMoney);

        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

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

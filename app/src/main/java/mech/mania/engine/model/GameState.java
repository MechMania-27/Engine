package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.config.Config;

public class GameState {
    @Expose
    private int turn = 1;
    @Expose
    private Player player1;
    @Expose
    private Player player2;
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

        this.player1 = player1;
        this.player2 = player2;

        tileMap = new TileMap(gameConfig, player1, player2);
    }

    public GameState(GameState other) {
        this.player1 = other.getPlayer1();
        this.player2 = other.getPlayer2();
        this.tileMap = other.getTileMap();
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

}

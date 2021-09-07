package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mech.mania.engine.config.Config;

public class GameState {
    @Expose
    private int turn;
    @Expose
    @SerializedName("p1")
    private Player player1;
    @Expose
    @SerializedName("p2")
    private Player player2;
    @Expose
    private TileMap tileMap;
    @Expose
    private int playerNum;
    @Expose
    private String feedback = "";

    private Config gameConfig;

    public GameState(Config gameConfig,
                     String player1Name, ItemType player1Item, UpgradeType player1UpgradeType,
                     String player2Name, ItemType player2Item, UpgradeType player2UpgradeType) {
        this.gameConfig = gameConfig;

        this.turn = 1;
        int startingMoney = gameConfig.STARTING_MONEY;
        Position player1Position = new Position(0, 0);
        Position player2Position = new Position(gameConfig.BOARD_WIDTH - 1, 0);

        Player player1 = new Player(player1Name, 0, player1Position, player1Item, player1UpgradeType, startingMoney, gameConfig);
        Player player2 = new Player(player2Name, 1, player2Position, player2Item, player2UpgradeType, startingMoney, gameConfig);

        this.player1 = player1;
        this.player2 = player2;

        this.tileMap = new TileMap(gameConfig, player1, player2);
    }

    public GameState(GameState other) {
        this.gameConfig = other.gameConfig;
        this.turn = other.turn;
        this.player1 = new Player(other.player1);
        this.player2 = new Player(other.player2);
        this.tileMap = new TileMap(other.tileMap);
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

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getPlayer(int playerID) {
        if (playerID == 0) {
            return player1;
        }
        return player2;
    }

    public Player getOpponentPlayer(int playerID) {
        if (playerID == 0) {
            return player2;
        }
        return player1;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

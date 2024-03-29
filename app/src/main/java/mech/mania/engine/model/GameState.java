package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mech.mania.engine.config.Config;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> feedback = new ArrayList<>();

    private static Config gameConfig;

    public GameState(Config gameConfig,
                     String player1Name, ItemType player1Item, UpgradeType player1UpgradeType,
                     String player2Name, ItemType player2Item, UpgradeType player2UpgradeType) {
        GameState.gameConfig = gameConfig;

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
        this.turn = other.turn;
        this.player1 = new Player(other.player1);
        this.player2 = new Player(other.player2);
        this.tileMap = new TileMap(other.tileMap);
        this.feedback = new ArrayList<>(other.feedback);
        this.playerNum = other.playerNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameState)) {
            return false;
        }

        GameState other = (GameState) obj;
        if (turn != other.turn) return false;
        if (!player1.equals(other.player1)) return false;
        if (!player2.equals(other.player2)) return false;
        if (!tileMap.equals(other.tileMap)) return false;
        if (!feedback.equals(other.feedback)) return false;
        if (playerNum != other.playerNum) return false;
        return true;
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

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }
}

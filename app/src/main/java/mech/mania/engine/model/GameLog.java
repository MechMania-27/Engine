package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mech.mania.engine.core.PlayerEndState;

import java.util.ArrayList;
import java.util.List;

public class GameLog {
    @Expose
    private List<GameState> states;
    @Expose
    @SerializedName("p1_status")
    private PlayerEndState player1EndState;
    @Expose
    @SerializedName("p2_status")
    private PlayerEndState player2EndState;
    @Expose
    @SerializedName("p1_achievements")
    private List<String> player1Achievements;
    @Expose
    @SerializedName("p2_achievements")
    private List<String> player2Achievements;

    public GameLog() {
        this.states = new ArrayList<>();
    }

    public void addState(GameState state) {
        this.states.add(state);
    }

    public PlayerEndState getPlayer1EndState() {
        return player1EndState;
    }

    public void setPlayer1EndState(PlayerEndState player1EndState) {
        this.player1EndState = player1EndState;
    }

    public PlayerEndState getPlayer2EndState() {
        return player2EndState;
    }

    public void setPlayer2EndState(PlayerEndState player2EndState) {
        this.player2EndState = player2EndState;
    }

    public void setPlayer1Achievements(List<String> player1Achievements) {
        this.player1Achievements = player1Achievements;
    }

    public void setPlayer2Achievements(List<String> player2Achievements) {
        this.player2Achievements = player2Achievements;
    }
}

package mech.mania.engine.model;

import com.google.gson.annotations.Expose;
import mech.mania.engine.core.PlayerEndState;

import java.util.ArrayList;
import java.util.List;

public class GameLog {
    @Expose
    private List<GameState> states;
    @Expose
    private PlayerEndState player1EndState;
    @Expose
    private PlayerEndState player2EndState;

    public GameLog() {
        this.states = new ArrayList<>();
    }

    public GameLog(List<GameState> states) {
        this.states = states;
    }

    public List<GameState> getStates() {
        return states;
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
}

package mech.mania.engine.model;

import mech.mania.engine.core.Winner;

import java.util.ArrayList;
import java.util.List;

public class GameLog {
    private List<GameState> states;
    private Winner winner;

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

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }
}

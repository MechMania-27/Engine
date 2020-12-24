package mech.mania.engine.model;

import java.util.ArrayList;
import java.util.List;

public class GameLog {
    private List<GameState> states;

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
}

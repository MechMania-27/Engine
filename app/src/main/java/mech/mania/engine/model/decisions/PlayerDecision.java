package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;

public abstract class PlayerDecision {
    protected String playerID;

    public abstract void performAction(GameState state);
}

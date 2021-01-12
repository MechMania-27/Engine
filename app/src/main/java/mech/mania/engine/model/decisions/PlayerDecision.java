package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;

public abstract class PlayerDecision {
    protected int playerID;

    /**
     * Parses in data from an args string
     * @param args The arguments to the PlayerDecision (NOT including the action type itself)
     * @returns itself
     */
    public abstract PlayerDecision parse(String args);
    protected final String separatorRegEx = "[,\\s]\\s*";

    public abstract void performAction(GameState state);
}

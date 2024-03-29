package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;

public abstract class PlayerDecision {
    protected int playerID;
    protected final JsonLogger playerLogger;
    protected final JsonLogger engineLogger;

    public PlayerDecision(JsonLogger playerLogger, JsonLogger engineLogger) {
        this.playerLogger = playerLogger;
        this.engineLogger = engineLogger;
    }

    /**
     * Parses in data from an args string
     * @param args The arguments to the PlayerDecision (NOT including the action type itself)
     * @return itself
     */
    public abstract PlayerDecision parse(String args) throws PlayerDecisionParseException;
    protected final String separatorRegEx = "[,\\s]\\s*";

    public abstract void performAction(GameState state);
}

package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;

public class HarvestAction extends PlayerDecision {
    public HarvestAction(String playerID, String input) {
        this.playerID = playerID;

        if (!input.strip().equalsIgnoreCase("harvest")) {
            throw new IllegalArgumentException(String.format("Wrong class input, should be %s", input));
        }
    }

    public void performAction(GameState state) {
        // stub for now
    }
}

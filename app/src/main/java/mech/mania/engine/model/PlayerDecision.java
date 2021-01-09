package mech.mania.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that will be deserializable from the player's output that represents
 * the decision that the player made for the turn.
 */
public class PlayerDecision {

    enum ActionType {
        NONE,
        PLANT,
        HARVEST,
        BUY,
        SELL,
        USE_ITEM
    }

    private Position movePos;  // specifically for MOVE action
    private ActionType action;  // action that isn't MOVE
    private Item itemToUse;  // for use item
    private List<Plant> seeds;  // for plant, buy
    private List<Position> actionPositions;  // for move, plant, harvest

    public PlayerDecision(Position movePos, ActionType action) {
        this.movePos = movePos;
        this.action = action;
        this.itemToUse = null;
        this.seeds = null;
        this.actionPositions = null;
    }

    public Position getMovePos() { return movePos; }
    public ActionType getAction() { return action; }
    public Item getItemToUse() { return itemToUse; }
    public List<Plant> getSeeds() { return seeds; }
    public List<Position> getActionPositions() { return actionPositions; }

    public void setMovePos(Position movePos) {
        this.movePos = movePos;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public void setItemToUse(Item itemToUse) {
        this.itemToUse = itemToUse;
    }

    public void addSeed(Plant seeds) {
        if (this.seeds == null) {
            this.seeds = new ArrayList<>();
        }
        this.seeds.add(seeds);
    }

    public void addActionPos(Position actionPos) {
        if (this.actionPositions == null) {
            this.actionPositions = new ArrayList<>();
        }
        this.actionPositions.add(actionPos);
    }
}

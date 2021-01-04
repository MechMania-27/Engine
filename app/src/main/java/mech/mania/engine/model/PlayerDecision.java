package mech.mania.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that will be deserializable from the player's output that represents
 * the decision that the player made for the turn.
 */
public class PlayerDecision {

    public enum ActionType {
        NONE,
        PLANT,
        HARVEST,
        BUY,
        SELL,
        USE_ITEM
    }

    private Position movePos;  // specifically for MOVE action
    private ActionType action;  // action that isn't MOVE
    private ItemType itemToUse;  // for use item
    private List<Crop> crops;  // for plant, buy
    private List<Integer> buyAmounts;  // for buy
    private List<Position> actionPositions;  // for move, plant, harvest

    public PlayerDecision() {
        this.movePos = null;
        this.action = null;
        this.itemToUse = null;
        this.crops = null;
        this.actionPositions = null;
    }

    public Position getMovePos() { return movePos; }
    public ActionType getAction() { return action; }
    public ItemType getItemToUse() { return itemToUse; }
    public List<Crop> getCrops() { return crops; }
    public List<Integer> getBuyAmounts() { return buyAmounts; }
    public List<Position> getActionPositions() { return actionPositions; }

    public void setMovePos(Position movePos) {
        this.movePos = movePos;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public void setItemToUse(ItemType itemToUse) {
        this.itemToUse = itemToUse;
    }

    public void addBuyAmount(int buyAmount) {
        if (this.buyAmounts == null) {
            this.buyAmounts = new ArrayList<>();
        }
        this.buyAmounts.add(buyAmount);
    }

    public void addSeed(Crop seeds) {
        if (this.crops == null) {
            this.crops = new ArrayList<>();
        }
        this.crops.add(seeds);
    }

    public void addActionPosition(Position actionPos) {
        if (this.actionPositions == null) {
            this.actionPositions = new ArrayList<>();
        }
        this.actionPositions.add(actionPos);
    }
}

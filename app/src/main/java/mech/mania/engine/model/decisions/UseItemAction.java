package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.ItemType;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.Position;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UseItemAction extends PlayerDecision {
    private ItemType item;
    private Position actionPosition;

    public UseItemAction(int playerID) {
        this.playerID = playerID;
        this.item = ItemType.NONE;
        this.actionPosition = null;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<item>[a-z|A-Z]+)" + separatorRegEx + "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);


        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            actionPosition = new Position(x, y);
            item = ItemType.getEnum(matcher.group("item"));
        } else{
            throw new PlayerDecisionParseException("Arguments did not match UseItem regex");
        }

        return this;
    }

    public void performAction(GameState state) {
        // stub for now
    }
}

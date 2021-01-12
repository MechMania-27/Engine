package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Position;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveAction extends PlayerDecision {
    private Position destination;

    public MoveAction(int playerID) {
        this.playerID = playerID;
        this.destination = null;
    }

    public PlayerDecision parse(String args) {
        String regex = "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            destination = new Position(x, y);
        } else{
            throw new IllegalArgumentException("Arguments did not match Move regex");
        }

        return this;
    }

    public void performAction(GameState state) {
        // stub for now
    }
}

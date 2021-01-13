package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.CropType;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyAction extends PlayerDecision {
    private ArrayList<CropType> seeds;
    private ArrayList<Integer> quantities;

    public BuyAction(int playerID) {
        this.playerID = playerID;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<seed>[a-z|A-Z]+)" + separatorRegEx + "(?<quantity>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        seeds = new ArrayList<>();
        quantities = new ArrayList<>();

        // Command must have at least one result
        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match Buy regex");
        }

        do {
            seeds.add(CropType.getEnum(matcher.group("seed")));
            quantities.add(Integer.parseInt(matcher.group("quantity")));
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state, JsonLogger engineLogger) {
        // stub for now
    }
}

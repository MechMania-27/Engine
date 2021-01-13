package mech.mania.engine.model.decisions;

import mech.mania.engine.model.CropType;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.Position;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantAction extends PlayerDecision {
    private ArrayList<Position> coords;
    private ArrayList<CropType> cropTypes;

    public PlantAction(int playerID){
        this.playerID = playerID;
        this.coords = new ArrayList<>();
        this.cropTypes = new ArrayList<>();
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "(?<crop>[a-z|A-Z]+)" + separatorRegEx + "(?<x>\\d+)" + separatorRegEx + "(?<y>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        coords = new ArrayList<>();
        cropTypes = new ArrayList<>();

        // Command must have at least one result
        if (!matcher.find()) {
            throw new PlayerDecisionParseException("Arguments did not match Plant regex");
        }

        do {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            coords.add(new Position(x, y));
            cropTypes.add(CropType.getEnum(matcher.group("crop")));
        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state) {
        // stub for now
        // will use playerID to get the Player object from state and then validate each planting action
    }
}

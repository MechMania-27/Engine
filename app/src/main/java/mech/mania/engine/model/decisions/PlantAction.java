package mech.mania.engine.model.decisions;

import mech.mania.engine.model.GameState;
import mech.mania.engine.model.CropType;

import java.util.ArrayList;

public class PlantAction extends PlayerDecision {
    private ArrayList<Integer> xCoords;
    private ArrayList<Integer> yCoords;
    private ArrayList<CropType> cropTypes;

    public PlantAction(String playerID, String input) {
        this.playerID = playerID;

        String[] words = input.split(";");
        if (words.length < 2) {
            throw new IllegalArgumentException("Input is too short");
        }
        if (!words[0].equalsIgnoreCase("plant")) {
            throw new IllegalArgumentException(String.format("Wrong class input, should be %s", words[0]));
        }

        xCoords = new ArrayList<>();
        yCoords = new ArrayList<>();
        cropTypes = new ArrayList<>();

        for (int i = 1; i < words.length; i++) {
            String[] tup = words[i].split(",");
            if (tup.length != 3) {
                continue;
            }
            xCoords.add(Integer.parseInt(tup[0]));
            yCoords.add(Integer.parseInt(tup[1]));
            cropTypes.add(CropType.valueOf(tup[2]));
        }
    }

    public void performAction(GameState state) {
        // stub for now
        // will use playerID to get the Player object from state and then validate each planting action
    }
}

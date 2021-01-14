package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;

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
        Player player = state.getPlayer(playerID);
        TileType curTile = state.getTileMap().getTileType(player.getPosition());
        if (curTile != TileType.GREEN_GROCER) {
            engineLogger.severe(String.format("Player %d failed to purchase, not on Green Grocer tile", playerID + 1));
            return;
        }

        int runningCost = 0;
        for (int i = 0; i < seeds.size(); i++) {
            runningCost += seeds.get(i).getSeedBuyPrice() * quantities.get(i);
        }

        if (runningCost > player.getMoney()) {
            engineLogger.severe(
                                String.format(
                                                "Player %d failed to purchase, price %d higher than budget %d",
                                                playerID + 1,
                                                runningCost,
                                                player.getMoney()));
            return;
        }

        for (int i = 0; i < seeds.size(); i++) {
            player.addSeeds(seeds.get(i), quantities.get(i));
        }

        player.setMoney(player.getMoney() - runningCost);
    }
}

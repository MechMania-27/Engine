package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyAction extends PlayerDecision {
    protected ArrayList<CropType> seeds;
    protected ArrayList<Integer> quantities;

    public BuyAction(int playerID, JsonLogger playerLogger, JsonLogger engineLogger) {
        super(playerLogger, engineLogger);
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
            try {
                quantities.add(Integer.parseInt(matcher.group("quantity")));
            } catch (NumberFormatException e) {
                throw new PlayerDecisionParseException("Value was of incorrect format");
            }

        } while (matcher.find());

        return this;
    }

    public void performAction(GameState state) {
        Player player = state.getPlayer(playerID);
        TileType curTile = state.getTileMap().getTileType(player.getPosition());
        if (curTile != TileType.GREEN_GROCER) {
            engineLogger.severe(String.format("Player %d failed to purchase, not on Green Grocer tile", playerID + 1));
            return;
        }

        for (int i = 0; i < seeds.size(); i++) {
            int cost = seeds.get(i).getSeedBuyPrice() * quantities.get(i);
            if (cost > player.getMoney()) {
                engineLogger.severe(
                                String.format(
                                                "Player %d failed to purchase %d %s seeds, budget %.2f, cost %d",
                                                playerID + 1,
                                                quantities.get(i),
                                                seeds.get(i),
                                                player.getMoney(),
                                                cost)
                );
                continue;
            }
            player.addSeeds(seeds.get(i), quantities.get(i));
            player.changeBalance(-cost);
            Achievements achievements = player.getAchievements();
            achievements.spendMoney(cost);

            engineLogger.info(String.format("Player %d bought %d %s seeds",
                    playerID + 1, quantities.get(i), seeds.get(i)));

        }

    }
}

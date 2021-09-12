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
        if (!player.getHasDeliveryDrone() && curTile != TileType.GREEN_GROCER) {
            String message = "Failed to purchase, not on Green Grocer" +
                    "tile and no delivery drone";
            playerLogger.feedback(message);
            engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
            return;
        }

        for (int i = 0; i < seeds.size(); i++) {
            double cost = 0;

            Achievements achievements = player.getAchievements();

            if (player.getUpgrade() != UpgradeType.LOYALTY_CARD) {
                cost = seeds.get(i).getSeedBuyPrice() * quantities.get(i);

                if (cost > player.getMoney()) {
                    String message = String.format("Failed to purchase %d %s seeds (amount before loyalty card takes effect), budget $%.2f, cost $%.2f",
                            quantities.get(i),
                            seeds.get(i),
                            player.getMoney(),
                            cost);
                    playerLogger.feedback(message);
                    engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                    continue;
                }

                player.addSeeds(seeds.get(i), quantities.get(i));
                player.changeBalance(-cost);
                achievements.spendMoney(cost);

            } else {
                // if buying more than the minimum amount for green grocer loyalty card, then
                // first deal with the seeds that are bought that are below the minimum
                int seedsToBuyBeforeMinimum = Math.min(
                        (int) Math.ceil(Math.max(player.getConfig().GREEN_GROCER_LOYALTY_CARD_MINIMUM - achievements.getMoneySpent(), 0)
                                / seeds.get(i).getSeedBuyPrice()
                        ),
                        quantities.get(i)
                );
                double costBeforeMinimum = seeds.get(i).getSeedBuyPrice() * seedsToBuyBeforeMinimum;

                // then the seeds that will go above
                int seedsToBuyAfterMinimum = quantities.get(i) - seedsToBuyBeforeMinimum;
                double costAfterMinimum = seeds.get(i).getSeedBuyPrice() * seedsToBuyAfterMinimum * (1 - player.getConfig().GREEN_GROCER_LOYALTY_CARD_DISCOUNT);

                double totalCost = costBeforeMinimum + costAfterMinimum;

                if (totalCost > player.getMoney()) {
                    String message = String.format("Failed to purchase %d %s seeds, budget $%.2f, cost $%.2f",
                            quantities.get(i),
                            seeds.get(i),
                            player.getMoney(),
                            costBeforeMinimum);
                    playerLogger.feedback(message);
                    engineLogger.severe(String.format("Player %d: " + message, playerID + 1));
                    continue;
                }

                player.addSeeds(seeds.get(i), quantities.get(i));
                player.changeBalance(-totalCost);
                achievements.spendMoney(totalCost);
            }

            String message = String.format("Bought %d %s seeds for $%.2f", quantities.get(i), seeds.get(i), cost);
            playerLogger.feedback(message);
            engineLogger.info(String.format("Player %d: " + message, playerID + 1));
        }

    }
}

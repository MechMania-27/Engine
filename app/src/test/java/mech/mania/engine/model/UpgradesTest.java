package mech.mania.engine.model;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.decisions.HarvestAction;
import org.junit.Assert;
import org.junit.Test;

public class UpgradesTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger(0);
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    private final static ItemType MY_PLAYER_ITEM = ItemType.NONE;
    private final static ItemType OPPONENT_PLAYER_ITEM = ItemType.NONE;

    @Test
    public void testUpgradeAttributesSet() {
        // LOYALTY CARD => DISCOUNT
        UpgradeType myPlayerUpgrade = UpgradeType.LOYALTY_CARD;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;
        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(0, state.getPlayer1().getDiscount());

        // BACKPACK => CARRYING CAPACITY
        myPlayerUpgrade = UpgradeType.BACKPACK;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(GAME_CONFIG.CARRYING_CAPACITY, state.getPlayer1().getCarryingCapacity());

        // LONGER LEGS => MAX MOVEMENT
        myPlayerUpgrade = UpgradeType.LONGER_LEGS;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(GAME_CONFIG.MAX_MOVEMENT, state.getPlayer1().getMaxMovement());

        // LONGER SCYTHE => HARVEST RADIUS
        myPlayerUpgrade = UpgradeType.LONGER_SCYTHE;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(GAME_CONFIG.HARVEST_RADIUS, state.getPlayer1().getHarvestRadius());

        // RABBITS FOOT => DROP CHANCE
        myPlayerUpgrade = UpgradeType.RABBITS_FOOT;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(0, state.getPlayer1().getDoubleDropChance());

        // SEED A PULT => PLANT RADIUS
        myPlayerUpgrade = UpgradeType.SEED_A_PULT;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(GAME_CONFIG.PLANT_RADIUS, state.getPlayer1().getPlantingRadius());

        // SPYGLASS => PROTECTION RADIUS
        myPlayerUpgrade = UpgradeType.SPYGLASS;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);

        Assert.assertNotEquals(GAME_CONFIG.PROTECTION_RADIUS, state.getPlayer1().getProtectionRadius());
    }

    @Test
    public void testDoubleDropChance() throws PlayerDecisionParseException {
        UpgradeType myPlayerUpgrade = UpgradeType.RABBITS_FOOT;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        int x = 0;
        int y = 3;
        HarvestAction action = new HarvestAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        action.parse(String.format("%d %d", x, y));

        GameState state;
        int numberOfTimesHarvested = 0;
        int numberOfHarvestedCrops = 0;
        for (int i = 0; i < 1000; i++) {
            state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, MY_PLAYER_ITEM, myPlayerUpgrade,
                    OPPONENT_PLAYER_NAME, OPPONENT_PLAYER_ITEM, opponentPlayerUpgrade);
            state.getPlayer1().setPosition(new Position(x, y));
            state.getTileMap().get(x, y).setCrop(new Crop(CropType.GRAPE, GAME_CONFIG));
            state.getTileMap().get(x, y).getCrop().setGrowthTimer(0);
            action.performAction(state);
            numberOfTimesHarvested++;
            numberOfHarvestedCrops += state.getPlayer1().getHarvestedCrops().size();
        }

        Assert.assertTrue(numberOfTimesHarvested < numberOfHarvestedCrops);
        Assert.assertEquals(numberOfTimesHarvested * (1 + GAME_CONFIG.RABBITS_FOOT_DOUBLE_DROP_CHANCE), numberOfHarvestedCrops, 100);
    }

}

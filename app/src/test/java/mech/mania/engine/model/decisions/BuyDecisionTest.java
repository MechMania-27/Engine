package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class BuyDecisionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger(0);
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    BuyDecision action;
    GameState state;

    @Before
    public void setup() {
        action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.LOYALTY_CARD;

        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(state.getTileMap().getGreenGrocer().get(0));
    }

    @Test
    public void buySingleCrop() throws PlayerDecisionParseException {
        String regularDecision = "corn 10";
        action.parse(regularDecision);

        Assert.assertEquals(1, action.seeds.size());
        Assert.assertEquals(CropType.CORN, action.seeds.get(0));
        Assert.assertEquals(1, action.quantities.size());
        Assert.assertEquals(10, (int) action.quantities.get(0));
    }

    @Test
    public void buyMultipleCrops() throws PlayerDecisionParseException {
        String multipleCropsBuyDecision = "corn 10 potato 20";
        action.parse(multipleCropsBuyDecision);

        Assert.assertEquals(2, action.seeds.size());
        Assert.assertArrayEquals(new CropType[]{CropType.CORN, CropType.POTATO}, action.seeds.toArray());
        Assert.assertEquals(2, action.quantities.size());
        Assert.assertArrayEquals(new Integer[]{10, 20}, action.quantities.toArray());
    }

    @Test
    public void buyNegativeQuantity() {
        String negativeBuyDecision = "corn -1";

        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeBuyDecision));
    }

    @Test
    public void buyOverMax() {
        String bigIntBuyDecision = "corn 2147483648";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(bigIntBuyDecision));
    }

    @Test
    public void buyInvalidString() {
        String noCropDecision = "";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(noCropDecision));

        String invalidFormatDecision = "corn";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(invalidFormatDecision));
    }

    @Test
    public void singleCropPerformAction() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice() * 10);

        BuyDecision action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 10";
        action.parse(decision);
        action.performAction(state);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(10, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void multipleCropPerformAction() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(
                                            CropType.CORN.getSeedBuyPrice() * 10
                                            + CropType.POTATO.getSeedBuyPrice() * 20
                                            + 5);

        BuyDecision action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(10, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(20, (int) seedInventory.get(CropType.POTATO));
        Assert.assertEquals(5, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void notEnoughMoneyBuyActionPerformAction() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(2);

        BuyDecision action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 2";
        action.parse(decision);
        action.performAction(state);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(0, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(2, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void partialPurchaseMultipleCrop() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice() * 10 + 3);

        BuyDecision action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(10, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(0, (int) seedInventory.get(CropType.POTATO));
        Assert.assertEquals(3, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void notOnGreenGrocerMoneyBuyActionPerformActionTest() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice() * 10);
        // one row below the green grocer rows
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(0, GAME_CONFIG.GRASS_ROWS));

        BuyDecision action = new BuyDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(0, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(0, (int) seedInventory.get(CropType.POTATO));
        Assert.assertEquals(CropType.CORN.getSeedBuyPrice() * 10, state.getPlayer(MY_PLAYER_ID).getMoney(), 0.01);
    }

    @Test
    public void smallPurchaseNoDiscount() throws PlayerDecisionParseException {
        state.getPlayer(OPPONENT_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice());
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(state.getTileMap().getGreenGrocer().get(0));

        BuyDecision action = new BuyDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "corn 1";
        action.parse(decision);
        action.performAction(state);

        Assert.assertEquals(0, state.getPlayer(OPPONENT_PLAYER_ID).getDiscount(), 0.001);
    }

    @Test
    public void largePurchaseNewDiscount() throws PlayerDecisionParseException {
        int amountToBuy = (int) (GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM / CropType.GRAPE.getSeedBuyPrice()) + 1;

        state.getPlayer(OPPONENT_PLAYER_ID).setMoney(CropType.GRAPE.getSeedBuyPrice() * amountToBuy);
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(state.getTileMap().getGreenGrocer().get(0));

        BuyDecision action = new BuyDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = String.format("grape %d", amountToBuy);
        action.parse(decision);
        action.performAction(state);

        Assert.assertEquals(
                GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_DISCOUNT,
                state.getPlayer(OPPONENT_PLAYER_ID).getDiscount(),
                0.001);
    }

    @Test
    public void discountApplied() throws PlayerDecisionParseException {
        state.getPlayer(OPPONENT_PLAYER_ID).setMoney(CropType.GRAPE.getSeedBuyPrice() * 20);
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(state.getTileMap().getGreenGrocer().get(0));

        BuyDecision action = new BuyDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        String decision = "grape 20";
        action.parse(decision);
        action.performAction(state);

        // 20 * 15 = 300 cost without loyalty card
        // 2 * 15 = 30 cost before loyalty card takes effect
        // 18 * (15 * 0.95) = 256.5 after loyalty card discount
        // remaining = 13.5
        Assert.assertEquals(13.5, state.getPlayer(OPPONENT_PLAYER_ID).getMoney(), 0.001);

        state.getPlayer(OPPONENT_PLAYER_ID).setMoney(CropType.GRAPE.getSeedBuyPrice() * 20);
        action.performAction(state);
        Assert.assertNotEquals(0, state.getPlayer(OPPONENT_PLAYER_ID).getMoney(), 0.001);
    }

    @Test
    public void testLoyaltyCardDiscount() throws PlayerDecisionParseException {
        // spend $GREEN_GROCER_LOYALTY_CARD_MINIMUM, buy seeds $GREEN_GROCER_LOYALTY_CARD_DISCOUNT % less
        int numSeedsToBuy = (int) Math.ceil(GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM / CropType.GRAPE.getSeedBuyPrice()) * 2;
        double moneyToSpend = (double) (numSeedsToBuy / 2) * CropType.GRAPE.getSeedBuyPrice() * (2 - GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_DISCOUNT);
        state.getPlayer2().setMoney(moneyToSpend);
        state.getPlayer2().setPosition(state.getTileMap().getGreenGrocer().get(0));
        BuyDecision action = new BuyDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);

        String decision = String.format("grape %d", numSeedsToBuy);
        action.parse(decision);
        action.performAction(state);

        Assert.assertEquals(0, state.getPlayer2().getMoney(), 0.001);
        Assert.assertEquals(moneyToSpend, state.getPlayer2().getAchievements().getMoneySpent(), 0.001);
    }

    @Test
    public void testLoyaltyCardDiscountMultiplePurchases() throws PlayerDecisionParseException {
        // spend $GREEN_GROCER_LOYALTY_CARD_MINIMUM, buy seeds $GREEN_GROCER_LOYALTY_CARD_DISCOUNT % less
        state.getPlayer2().setMoney(GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM * 2);
        state.getPlayer2().setPosition(state.getTileMap().getGreenGrocer().get(0));
        BuyDecision action = new BuyDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);

        int numSeedsToBuy = (int) Math.ceil(GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM / CropType.GRAPE.getSeedBuyPrice()) / 2;
        String decision = String.format("grape %d", numSeedsToBuy);
        action.parse(decision);
        action.performAction(state);

        Assert.assertEquals(0, state.getPlayer2().getDiscount(), 0.001);

        numSeedsToBuy = (int) Math.ceil(GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM / CropType.GRAPE.getSeedBuyPrice()) - numSeedsToBuy;
        decision = String.format("grape %d", numSeedsToBuy);
        action.parse(decision);
        action.performAction(state);

        Assert.assertNotEquals(0, state.getPlayer2().getDiscount(), 0.001);

        numSeedsToBuy = (int) Math.ceil(GAME_CONFIG.GREEN_GROCER_LOYALTY_CARD_MINIMUM / CropType.GRAPE.getSeedBuyPrice());
        decision = String.format("grape %d", numSeedsToBuy);
        action.parse(decision);
        action.performAction(state);

        Assert.assertNotEquals(0, state.getPlayer2().getMoney());
        Assert.assertNotEquals(0, state.getPlayer2().getAchievements().getMoneySpent());
    }
}

package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class BuyActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    BuyAction action;
    GameState state;

    @Before
    public void setup() {
        action = new BuyAction(MY_PLAYER_ID);
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

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

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "corn 10";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

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

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(10, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(20, (int) seedInventory.get(CropType.POTATO));
        Assert.assertEquals(5, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void notEnoughMoneyBuyActionPerformAction() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(2);

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "corn 2";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(0, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(2, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void partialPurchaseMultipleCrop() throws PlayerDecisionParseException {
        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice() * 10 + 3);

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

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

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "corn 10 potato 20";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(0, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(0, (int) seedInventory.get(CropType.POTATO));
        Assert.assertEquals(CropType.CORN.getSeedBuyPrice() * 10, state.getPlayer(MY_PLAYER_ID).getMoney(), 0.01);
    }
}

package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class BuyActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    @Test
    public void buyActionParseDecisionTest() throws PlayerDecisionParseException {
        BuyAction action = new BuyAction(MY_PLAYER_ID);

        String regularDecision = "buy corn 10";
        action.parse(regularDecision);
        Assert.assertEquals(1, action.seeds.size());
        Assert.assertEquals(CropType.CORN, action.seeds.get(0));
        Assert.assertEquals(1, action.quantities.size());
        Assert.assertEquals(10, (int) action.quantities.get(0));

        String multipleCropsBuyDecision = "buy corn 10 potato 20";
        action.parse(multipleCropsBuyDecision);
        Assert.assertEquals(2, action.seeds.size());
        Assert.assertArrayEquals(new CropType[]{CropType.CORN, CropType.POTATO}, action.seeds.toArray());
        Assert.assertEquals(2, action.quantities.size());
        Assert.assertArrayEquals(new Integer[]{10, 20}, action.quantities.toArray());

        String negativeBuyDecision = "buy corn -1";
        action.parse(negativeBuyDecision);
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeBuyDecision));

        String bigIntBuyDecision = String.format("buy corn %d", (long) Integer.MAX_VALUE + 1);
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(bigIntBuyDecision));

        // TODO: buying zero?
    }

    @Test
    public void regularBuyActionPerformActionTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice() * 10);

        BuyAction action = new BuyAction(MY_PLAYER_ID);
        String decision = "buy corn 10";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

        Map<CropType, Integer> seedInventory = state.getPlayer(MY_PLAYER_ID).getSeeds();
        Assert.assertEquals(1, seedInventory.size());
        Assert.assertEquals(10, (int) seedInventory.get(CropType.CORN));
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);
    }

    @Test
    public void notEnoughMoneyBuyActionPerformActionTest() throws PlayerDecisionParseException {

    }

    @Test
    public void notOnGreenGrocerMoneyBuyActionPerformActionTest() throws PlayerDecisionParseException {

    }
}

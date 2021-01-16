package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Test;

public class HarvestActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    @Test
    public void buyActionParseDecisionTest() throws PlayerDecisionParseException {
        HarvestAction action = new HarvestAction(MY_PLAYER_ID);

        String regularDecision = "harvest corn 1 1";
        action.parse(regularDecision);
        // Assert.assertEquals(1, action.seeds.size());
        // Assert.assertEquals(CropType.CORN, action.seeds.get(0));
        // Assert.assertEquals(1, action.quantities.size());
        // Assert.assertEquals(10, (int) action.quantities.get(0));

        String negativeBuyDecision = "harvest corn -1 -1";
        // Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeBuyDecision));

        String bigIntBuyDecision = String.format("harvest corn %d %d", (long) Integer.MAX_VALUE + 1, (long) Integer.MAX_VALUE + 1);
        // Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(bigIntBuyDecision));

        // TODO: buying zero?
    }

    @Test
    public void regularHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        HarvestAction action = new HarvestAction(MY_PLAYER_ID);
        String decision = "buy corn 10";
        action.parse(decision);
        action.performAction(state, BOT_LOGGER);

        // Assert.assertEquals();
    }

    @Test
    public void outsideHarvestRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {

    }

    @Test
    public void carryingCapacityHarvestActionPerformActionTest() throws PlayerDecisionParseException {

    }

    @Test
    public void noCropHarvestActionPerformActionTest() throws PlayerDecisionParseException {

    }

    @Test
    public void unripeCropHarvestActionPerformActionTest() throws PlayerDecisionParseException {

    }

    @Test
    public void insideProtectionRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {

    }
}

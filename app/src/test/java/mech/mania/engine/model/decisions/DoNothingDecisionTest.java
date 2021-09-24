package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DoNothingDecisionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger(0);

    DoNothingDecision action;
    GameState state;

    @Before
    public void setup() {
        action = new DoNothingDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);

        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);
    }

    @Test
    public void doNothingTest() throws PlayerDecisionParseException {
        action.parse("");
        GameState newState = new GameState(state);
        action.performAction(newState);
        Assert.assertEquals(state, newState);
    }
}

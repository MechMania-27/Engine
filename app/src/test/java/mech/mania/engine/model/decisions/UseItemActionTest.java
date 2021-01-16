package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.PlayerDecisionParseException;
import org.junit.Test;

public class UseItemActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    @Test
    public void useItemActionParseDecisionTest() throws PlayerDecisionParseException {
        // see other actions
    }

    @Test
    public void regularUseItemActionPerformActionTest() throws PlayerDecisionParseException {

    }

    // TODO: add other tests once implementation for UseItemAction is complete
}

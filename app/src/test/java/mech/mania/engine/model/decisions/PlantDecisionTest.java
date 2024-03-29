package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlantDecisionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger();
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger();

    private final static ItemType myPlayerItem = ItemType.NONE;
    private final static UpgradeType myPlayerUpgrade = UpgradeType.NONE;
    private final static ItemType opponentPlayerItem = ItemType.NONE;
    private final static UpgradeType opponentPlayerUpgrade = UpgradeType.SEED_A_PULT;

    PlantDecision action;
    GameState state;

    @Before
    public void setup() {
        action = new PlantDecision(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);

        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);
    }

    @Test
    public void plantActionParseDecisionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 1 1";
        action.parse(regularDecision);
        Assert.assertEquals(1, action.coords.size());
        Assert.assertEquals(new Position(1, 1), action.coords.get(0));
        Assert.assertEquals(CropType.CORN, action.cropTypes.get(0));

        String negativeBuyDecision = "corn -1 -1";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeBuyDecision));

        String invalidBuyDecision = "corn hi hi";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(invalidBuyDecision));
    }

    @Test
    public void regularPlantActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(0, res);
        Assert.assertEquals(CropType.CORN, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void outsidePlantRadiusPlantActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(7, 7));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(1, res);
        Assert.assertEquals(CropType.NONE, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void notEnoughSeedsPlantActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

//        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(0, res);
        Assert.assertEquals(CropType.NONE, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void plantAlreadyExistsAtDestinationPlantActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

        state.getTileMap().plantCrop(new Position(5, 5), CropType.POTATO, state.getPlayer(1));

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(1, res);
        Assert.assertEquals(CropType.POTATO, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void insideOpponentProtectionRadiusPlantActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5));
        state.getOpponentPlayer(MY_PLAYER_ID).setPosition(new Position(6, 6));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(1, res);
        Assert.assertEquals(CropType.NONE, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void multiplePlantPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5 grape 6 5";
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.GRAPE, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(0, res);
        res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.GRAPE);
        Assert.assertEquals(0, res);
        Assert.assertEquals(CropType.CORN, state.getTileMap().get(5, 5).getCrop().getType());
        Assert.assertEquals(CropType.GRAPE, state.getTileMap().get(6, 5).getCrop().getType());
    }

    @Test
    public void upgradePlantActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action = new PlantDecision(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        action.parse(regularDecision);

        state.getPlayer(OPPONENT_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(new Position(5, 5 + GAME_CONFIG.PLANT_RADIUS + 1));

        action.performAction(state);
        int res = state.getPlayer(OPPONENT_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(0, res);
        Assert.assertEquals(CropType.CORN, state.getTileMap().get(5, 5).getCrop().getType());
    }

    @Test
    public void outsideUpgradePlantActionTest() throws PlayerDecisionParseException {
        String regularDecision = "corn 5 5";
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).getSeeds().put(CropType.CORN, 1);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(5, 5 + GAME_CONFIG.SEED_A_PULT_PLANT_RADIUS + 1));

        action.performAction(state);
        int res = state.getPlayer(MY_PLAYER_ID).getSeeds().get(CropType.CORN);
        Assert.assertEquals(1, res);
        Assert.assertEquals(CropType.NONE, state.getTileMap().get(5, 5).getCrop().getType());
    }
}

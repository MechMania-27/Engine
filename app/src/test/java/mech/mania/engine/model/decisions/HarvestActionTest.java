package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HarvestActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    HarvestAction action;
    GameState state;
    CropType[] types = {CropType.CORN, CropType.POTATO, CropType.NONE};

    @Before
    public void setup() {
        action = new HarvestAction(MY_PLAYER_ID);
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        int width = GAME_CONFIG.BOARD_WIDTH;
        int height = GAME_CONFIG.BOARD_HEIGHT;

        for (int i = width / 4; i < 3 * width / 4; i++) {
            for (int j = height / 4; j < 3 * height / 4; j++) {
                CropType curCrop = types[(i + j) % types.length];
                state.getTileMap().plantCrop(new Position(i, j), curCrop);
            }
        }
    }

    @Test
    public void harvestActionParseDecisionTest() throws PlayerDecisionParseException {
        String regularDecision = "1 1";
        action.parse(regularDecision);
        Assert.assertEquals(1, action.coords.size());
        Assert.assertEquals(action.coords.get(0), new Position(1, 1));

        String negativeBuyDecision = "corn -1 -1";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeBuyDecision));
    }

    @Test
    public void regularHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        // corn at this location- should be grown
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4);
        action.parse(regularDecision);
        state.getPlayer(MY_PLAYER_ID).setPosition(
                                new Position(GAME_CONFIG.BOARD_WIDTH / 4,
                                            GAME_CONFIG.BOARD_HEIGHT / 4));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state, BOT_LOGGER);
        Assert.assertEquals(1, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
        Assert.assertEquals(CropType.POTATO, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().get(0).getType());

        Assert.assertEquals(
                CropType.NONE,
                state.getTileMap()
                        .get(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4)
                        .getCrop()
                        .getType());
    }

    @Test
    public void outsideHarvestRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4);
        action.parse(regularDecision);

        System.out.println(action.coords.get(0));

        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4 + state.getPlayer(MY_PLAYER_ID).getHarvestRadius() + 1,
                        GAME_CONFIG.BOARD_HEIGHT / 4 + 1));

        System.out.println(state.getPlayer(MY_PLAYER_ID).getPosition());

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state, BOT_LOGGER);
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());

        Assert.assertEquals(
                CropType.POTATO,
                state.getTileMap()
                        .get(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4)
                        .getCrop()
                        .getType());
    }

    @Test
    public void carryingCapacityHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        StringBuilder builder = new StringBuilder();
//        builder.append("");

        for (int i = GAME_CONFIG.BOARD_WIDTH / 4; i < GAME_CONFIG.BOARD_WIDTH / 4 + 5; i++) {
            for (int j = GAME_CONFIG.BOARD_HEIGHT / 4; j < GAME_CONFIG.BOARD_HEIGHT / 4 + 5; j++) {
                builder.append(String.format("%d %d ", i, j));
                System.out.println(state.getTileMap().get(i, j).getCrop().getType());
            }
        }

        for (int i = 0; i < 10; i++) {
            state.getTileMap().growCrops();
        }

        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4 + 2,
                        GAME_CONFIG.BOARD_HEIGHT / 4 + 2));

        String regularDecision = builder.toString();
        action.parse(regularDecision);

        System.out.println(action.coords);

        action.performAction(state, BOT_LOGGER);

        Assert.assertEquals(
                    state.getPlayer(MY_PLAYER_ID).getCarryingCapacity(),
                    state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
    }

    @Test
    public void noCropHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        Position noCropPos = null;
        for (int i = GAME_CONFIG.BOARD_WIDTH / 4; i < 3 * GAME_CONFIG.BOARD_WIDTH / 4; i++) {
            for (int j = GAME_CONFIG.BOARD_HEIGHT / 4; j < 3 * GAME_CONFIG.BOARD_HEIGHT / 4; j++) {
                if (noCropPos == null && state.getTileMap().get(i, j).getCrop().getType() == CropType.NONE) {
                    noCropPos = new Position(i, j);
                    break;
                }
            }
        }

        Assert.assertNotNull(noCropPos);

        String regularDecision = String.format("%d %d", noCropPos.getX(), noCropPos.getY());
        action.parse(regularDecision);

        state.getPlayer(MY_PLAYER_ID).setPosition(noCropPos);

        action.performAction(state, BOT_LOGGER);
        Assert.assertEquals(state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size(), 0);

    }

    @Test
    public void unripeCropHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        // corn at this location- should not be grown
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4);
        action.parse(regularDecision);
        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4,
                        GAME_CONFIG.BOARD_HEIGHT / 4));

        action.performAction(state, BOT_LOGGER);
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
//        Assert.assertEquals(CropType.CORN, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().get(0).getType());

    }

    @Test
    public void insideProtectionRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        // corn at this location- should be grown
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4);
        action.parse(regularDecision);
        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4,
                        GAME_CONFIG.BOARD_HEIGHT / 4));
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4 + 1,
                        GAME_CONFIG.BOARD_HEIGHT / 4));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state, BOT_LOGGER);
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
    }
}

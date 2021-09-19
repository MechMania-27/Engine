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
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger(0);

    HarvestAction action;
    GameState state;
    CropType[] types = {CropType.CORN, CropType.POTATO};

    @Before
    public void setup() {
        action = new HarvestAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        int width = GAME_CONFIG.BOARD_WIDTH;
        int height = GAME_CONFIG.BOARD_HEIGHT;

        for (int i = 0; i < width; i++) {
            for (int j = 3; j < height; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
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
        int x = 3, y = 3;

        // corn is planted at 3 3
        String regularDecision = String.format("%d %d", x, y);
        action.parse(regularDecision);
        state.getPlayer(MY_PLAYER_ID).setPosition(
                                new Position(x, y));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state);
        Assert.assertEquals(1, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
        Assert.assertEquals(CropType.CORN, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().get(0).getType());

        Assert.assertEquals(
                CropType.NONE,
                state.getTileMap()
                        .get(x, y)
                        .getCrop()
                        .getType());
    }

    @Test
    public void outsideHarvestRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4);
        action.parse(regularDecision);

        int x = 3;
        int y = 3;
        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(x + state.getPlayer(MY_PLAYER_ID).getHarvestRadius() + 1,
                        y + 1));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state);
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());

        Assert.assertNotSame(
                CropType.NONE,
                state.getTileMap()
                        .get(x, y)
                        .getCrop()
                        .getType());
    }

    @Test
    public void carryingCapacityHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        StringBuilder builder = new StringBuilder();

        for (int i = 3; i <= 7; i++) {
            for (int j = 3; j <= 7; j++) {
                builder.append(String.format("%d %d ", i, j));
            }
        }

        for (int i = 0; i < 10; i++) {
            state.getTileMap().growCrops();
        }

        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(5, 5));

        String regularDecision = builder.toString();
        action.parse(regularDecision);

        action.performAction(state);
//        System.out.println(action.coords);

        action.performAction(state);

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

        action.performAction(state);
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

        action.performAction(state);
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
        state.getTileMap().growCrops();

        action.performAction(state);
        Assert.assertEquals(0, state.getPlayer(MY_PLAYER_ID).getHarvestedCrops().size());
    }

    @Test
    public void insideScytheHarvestRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        int x = 3, y = 3;
        String regularDecision = String.format("%d %d", x, y);
        action = new HarvestAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        action.parse(regularDecision);

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(
                new Position(x + GAME_CONFIG.HARVEST_RADIUS - 1,
                        y));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state);

        Assert.assertEquals(1, state.getPlayer(OPPONENT_PLAYER_ID).getHarvestedCrops().size());
        Assert.assertEquals(CropType.CORN, state.getPlayer(OPPONENT_PLAYER_ID).getHarvestedCrops().get(0).getType());

        Assert.assertEquals(
                CropType.NONE,
                state.getTileMap()
                        .get(x, y)
                        .getCrop()
                        .getType());

    }

    @Test
    public void outsideScytheHarvestRadiusHarvestActionPerformActionTest() throws PlayerDecisionParseException {
        int x = 3, y = 3;
        String regularDecision = String.format("%d %d", x, y);
        action.parse(regularDecision);

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(
                new Position(x + state.getPlayer(OPPONENT_PLAYER_ID).getHarvestRadius() + 1,
                        y + 1));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state);
        Assert.assertEquals(0, state.getPlayer(OPPONENT_PLAYER_ID).getHarvestedCrops().size());

        Assert.assertEquals(
                CropType.CORN,
                state.getTileMap()
                        .get(x, y)
                        .getCrop()
                        .getType());
    }

    @Test
    public void insideOpponentProtectionRadiusTest() throws PlayerDecisionParseException {
        String regularDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH / 4 + GAME_CONFIG.PROTECTION_RADIUS + 1,
                                                GAME_CONFIG.BOARD_HEIGHT / 4);;
        action.parse(regularDecision);

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4 + GAME_CONFIG.PROTECTION_RADIUS + 1,
                        GAME_CONFIG.BOARD_HEIGHT / 4));

        state.getPlayer(MY_PLAYER_ID).setPosition(
                new Position(GAME_CONFIG.BOARD_WIDTH / 4 + GAME_CONFIG.PROTECTION_RADIUS + GAME_CONFIG.HARVEST_RADIUS,
                        GAME_CONFIG.BOARD_HEIGHT / 4));

        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();
        state.getTileMap().growCrops();

        action.performAction(state);
        Assert.assertEquals(0, state.getPlayer(OPPONENT_PLAYER_ID).getHarvestedCrops().size());
    }
}

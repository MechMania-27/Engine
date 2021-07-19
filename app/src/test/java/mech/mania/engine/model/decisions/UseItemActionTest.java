package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class UseItemActionTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);
    private UseItemAction action;
    private GameState state;

    private final static UpgradeType myPlayerUpgrade = UpgradeType.NONE;
    private final static UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

    private final static CropType[] types = {CropType.CORN, CropType.POTATO};

    @Before
    public void setup() {
        action = new UseItemAction(MY_PLAYER_ID);
    }

    @Test
    public void rainTotemUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.RAIN_TOTEM, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        int width = GAME_CONFIG.BOARD_WIDTH;
        int height = GAME_CONFIG.BOARD_HEIGHT;

        for (int i = width / 4; i < 3 * width / 4; i++) {
            for (int j = height / 4; j < 3 * height / 4; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
            }
        }

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");
        action.performAction(state, BOT_LOGGER);

        state.getTileMap().growCrops();

        Assert.assertEquals(1, state.getTileMap().get(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4).getCrop().getGrowthTimer());
    }

    @Test
    public void fertilityIdolUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.FERTILITY_IDOL, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");

        int x = GAME_CONFIG.BOARD_WIDTH / 4;
        int y = GAME_CONFIG.BOARD_HEIGHT / 4;

        int width = GAME_CONFIG.BOARD_WIDTH;
        int height = GAME_CONFIG.BOARD_HEIGHT;

        for (int i = width / 4; i < 3 * width / 4; i++) {
            for (int j = height / 4; j < 3 * height / 4; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
            }
        }

        action.performAction(state, BOT_LOGGER);

        for (int i = x - 2; i < x + 2; i++) {
            for (int j = y - 2; j < y + 2; j++) {
                Tile curTile = state.getTileMap().get(i, j);
                Assert.assertEquals(curTile.getFertility(), curTile.getType().getFertility() * 2, 0.001);
            }
        }

    }

    @Test
    public void pesticideUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.PESTICIDE, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);


        action.parse("");

        int width = GAME_CONFIG.BOARD_WIDTH;
        int height = GAME_CONFIG.BOARD_HEIGHT;

        int match_i, match_j = 0;
        boolean found = false;
        for (match_i = 0; match_i < width; match_i++) {
            for (match_j = 0; match_j < height; match_j++) {
                if (state.getTileMap().get(match_i, match_j).getType() == TileType.SOIL) {
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(match_i, match_j));


        for (int i = width / 4; i < 3 * width / 4; i++) {
            for (int j = height / 4; j < 3 * height / 4; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
            }
        }

        state.getTileMap().growCrops();

        action.performAction(state, BOT_LOGGER);

        Tile curTile = state.getTileMap().get(match_i, match_j);
        Assert.assertEquals(0.8 * curTile.getCrop().getType().getValueGrowth(), curTile.getCrop().getValue(), 0.001);
    }

    @Test
    public void coffeeThermosUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.COFFEE_THERMOS, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");

        action.performAction(state, BOT_LOGGER);
        Assert.assertTrue(state.getPlayer(MY_PLAYER_ID).getUseCoffeeThermos());
        Assert.assertEquals(GAME_CONFIG.MAX_MOVEMENT * 3, state.getPlayer(MY_PLAYER_ID).getSpeed());
    }

    @Test
    public void scarecrowUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.SCARECROW, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");

        action.performAction(state, BOT_LOGGER);

        int x = GAME_CONFIG.BOARD_WIDTH / 4;
        int y = GAME_CONFIG.BOARD_HEIGHT / 4;

        for (int i = x - 2; i < x + 2; i++) {
            for (int j = y - 2; j < y + 2; j++) {
                Tile curTile = state.getTileMap().get(i, j);
                Assert.assertEquals(0, curTile.isScarecrowEffect());
            }
        }
    }

    @Test
    public void droneUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.DELIVERY_DRONE, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");

        action.performAction(state, BOT_LOGGER);

        Assert.assertTrue(state.getPlayer(MY_PLAYER_ID).getDeliveryDrone());
    }


    // TODO: add other tests once implementation for UseItemAction is complete
}

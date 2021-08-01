package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

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

        // plant crops outside of player's rain totem range
        for (int i = 0; i <= 9; i++) {
            for (int j = 8; j <= 9; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
            }
        }

        // plant crops inside of player's rain totem range
        for (int i = 3; i <= 7; i++) {
            for (int j = 3; j <= 7; j++) {
                CropType curCrop = types[(i + j) % types.length];
                Player player = state.getPlayer(MY_PLAYER_ID);
                state.getTileMap().plantCrop(new Position(i, j), curCrop, player);
            }
        }

        int x = 5, y = 5;
        // move the player and use the rain totem item
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(x, y));
        action.parse("");
        action.performAction(state, BOT_LOGGER);

        // grow all crops (the ones within range of the rain totem will grow by 3)
        state.getTileMap().growCrops();

        for (int i = 0; i <= 9; i++) {
            for (int j = 8; j <= 9; j++) {
                CropType curCrop = types[(i + j) % types.length];
                // should grow all crops by 1
                int expectedTimer = Math.max(0, curCrop.getTimeToGrow() - 1);
                int actualTimer = state.getTileMap().get(i, j).getCrop().getGrowthTimer();
                Assert.assertEquals(expectedTimer, actualTimer);
            }
        }

        for (int i = 3; i <= 7; i++) {
            for (int j = 3; j <= 7; j++) {
                CropType curCrop = types[(i + j) % types.length];
                // should grow all crops by 3
                int expectedTimer = Math.max(0, curCrop.getTimeToGrow() - 3);
                int actualTimer = state.getTileMap().get(i, j).getCrop().getGrowthTimer();
                Assert.assertEquals(expectedTimer, actualTimer);
            }
        }
    }

    @Test
    public void fertilityIdolUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.FERTILITY_IDOL, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(GAME_CONFIG.BOARD_WIDTH / 4, GAME_CONFIG.BOARD_HEIGHT / 4));
        action.parse("");

        int x = GAME_CONFIG.BOARD_WIDTH / 2;
        int y = GAME_CONFIG.BOARD_HEIGHT / 2;

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

        action.performAction(state, BOT_LOGGER);

        for (int i = x - 2; i < x + 2; i++) {
            for (int j = y - 2; j < y + 2; j++) {
                Tile curTile = state.getTileMap().get(i, j);
                // does the fertility return to normal after the boosted turn?
                Assert.assertEquals(curTile.getFertility(), curTile.getType().getFertility() * 1, 0.001);
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

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(0, 0));
        action.parse("");

        action.performAction(state, BOT_LOGGER);
        Assert.assertTrue(state.getPlayer(MY_PLAYER_ID).getUseCoffeeThermos());
        Assert.assertEquals(GAME_CONFIG.MAX_MOVEMENT * 3, state.getPlayer(MY_PLAYER_ID).getSpeed());

        // see if move actions will work with the new movement
        MoveAction newAction = new MoveAction(MY_PLAYER_ID);
        // movement becomes 6 (was 2 before), distance is counted using manhattan distance
        int newX = state.getPlayer(MY_PLAYER_ID).getPosition().getX() + GAME_CONFIG.MAX_MOVEMENT * 3;
        int newY = state.getPlayer(MY_PLAYER_ID).getPosition().getY();
        newAction.parse(String.format("%d %d", newX, newY));
        newAction.performAction(state, BOT_LOGGER);

        Assert.assertEquals(new Position(newX, newY), state.getPlayer(MY_PLAYER_ID).getPosition());
    }

    @Test
    public void scarecrowUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.SCARECROW, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        int x = GAME_CONFIG.BOARD_WIDTH / 4;
        int y = GAME_CONFIG.BOARD_HEIGHT / 4;

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(x, y));
        action.parse("");

        action.performAction(state, BOT_LOGGER);

        for (int i = x - 2; i < x + 2; i++) {
            for (int j = y - 2; j < y + 2; j++) {
                Tile curTile = state.getTileMap().get(i, j);
                Assert.assertEquals(0, curTile.isScarecrowEffect());
            }
        }

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(new Position(x - 1, y - 1));
        state.getPlayer(OPPONENT_PLAYER_ID).addSeeds(CropType.CORN, 1);
        Assert.assertEquals(CropType.NONE, state.getTileMap().getTile(new Position(x - 1, y - 1)).getCrop().getType());

        PlantAction opponentAction = new PlantAction(OPPONENT_PLAYER_ID);
        opponentAction.parse(String.format("corn %d %d", x - 1, y - 1));
        opponentAction.performAction(state, BOT_LOGGER);
        // make sure nothing was planted
        Assert.assertEquals(CropType.NONE, state.getTileMap().getTile(new Position(x - 1, y - 1)).getCrop().getType());
    }

    @Test
    public void droneUseTest() throws PlayerDecisionParseException {
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, ItemType.DELIVERY_DRONE, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, ItemType.NONE, opponentPlayerUpgrade);

        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(9, 9));

        action.parse("");
        action.performAction(state, BOT_LOGGER);

        Assert.assertTrue(state.getPlayer(MY_PLAYER_ID).getDeliveryDrone());
        // player should have 0 seeds
        Assert.assertEquals(Optional.of(0), state.getPlayer(MY_PLAYER_ID).getSeeds().values().stream().reduce(Integer::sum));

        state.getPlayer(MY_PLAYER_ID).setMoney(CropType.CORN.getSeedBuyPrice());
        BuyAction buyAction = new BuyAction(MY_PLAYER_ID);
        buyAction.parse("corn 1");
        buyAction.performAction(state, BOT_LOGGER);

        // sum up all of the different numbers of seeds that the player has, make sure they only have one
        Assert.assertEquals(Optional.of(1), state.getPlayer(MY_PLAYER_ID).getSeeds().values().stream().reduce(Integer::sum));
    }
}

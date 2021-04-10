package mech.mania.engine.model;

import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.decisions.MoveAction;
import mech.mania.engine.model.decisions.PlantAction;
import mech.mania.engine.model.decisions.PlayerDecision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CropTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);

    private static GameState state;

    @Before
    public void setup() {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;
        state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);
        state.getPlayer(MY_PLAYER_ID).setPosition(new Position(3, 3));
        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(new Position(6, 3));
    }

    @Test
    public void profitMarginTest() throws PlayerDecisionParseException {
        JsonLogger engineLogger = new JsonLogger(0);

        Map<CropType, Double> expectedProfitMargin = new HashMap<>();
        expectedProfitMargin.put(CropType.POTATO, 1.1);
        expectedProfitMargin.put(CropType.CORN, 1.0);
        expectedProfitMargin.put(CropType.GRAPE, 1.1);
        expectedProfitMargin.put(CropType.JOGANFRUIT, 1.0);
        expectedProfitMargin.put(CropType.PEANUTS, 1.0);
        expectedProfitMargin.put(CropType.QUADROTRITICALE, 1.3);
        expectedProfitMargin.put(CropType.DUCHAMFRUIT, 1.0);
        expectedProfitMargin.put(CropType.GOLDENCORN, 1.5);

        for (Map.Entry<CropType, Double> entry : expectedProfitMargin.entrySet()) {
            int growthTime = entry.getKey().getGrowthTime();
            double growthValue = getGrowthValueAfterNTurns(entry.getKey(), growthTime, engineLogger);
            double originalPrice = entry.getKey().getSeedBuyPrice();
            double margin = (growthValue - originalPrice) / originalPrice;
            Assert.assertEquals(entry.getValue(), margin, 1e-3);
        }
    }

    private double getGrowthValueAfterNTurns(CropType type, int turns, JsonLogger engineLogger) throws PlayerDecisionParseException {
        // give player ability to plant
        state.getPlayer(MY_PLAYER_ID).addSeeds(type, 1);

        // plant crop
        PlayerDecision player1Decision = new PlantAction(MY_PLAYER_ID);
        player1Decision.parse(String.format("%s 3 3", type));
        PlayerDecision player2Decision = new MoveAction(OPPONENT_PLAYER_ID);
        player2Decision.parse("6 3");  // move to the same position (no action)
        GameState newState = GameLogic.updateGameState(state, player1Decision,
                player2Decision, GAME_CONFIG, engineLogger);
        engineLogger.incrementTurn();

        // check growth value
        Crop plantedCrop = newState.getTileMap().getTile(new Position(3, 3)).getCrop();
        Assert.assertEquals(type, plantedCrop.getType());
        Assert.assertEquals(0, plantedCrop.getValue(), 1e-3);

        for (int i = 0; i < turns; i++) {
            // advance turn
            player1Decision = new MoveAction(MY_PLAYER_ID);
            player1Decision.parse("3 3");  // move to the same position (no action)
            player2Decision = new MoveAction(OPPONENT_PLAYER_ID);
            player2Decision.parse("6 3");  // move to the same position (no action)
            newState = GameLogic.updateGameState(newState, player1Decision,
                    player2Decision, GAME_CONFIG, engineLogger);
            engineLogger.incrementTurn();
        }

        // check growth value
        plantedCrop = newState.getTileMap().getTile(new Position(3, 3)).getCrop();
        Assert.assertEquals(type, plantedCrop.getType());
        // growth formula: GVT * [(1 * (1 - FS)) + (TF * FS)]
        return plantedCrop.getValue();
    }
}

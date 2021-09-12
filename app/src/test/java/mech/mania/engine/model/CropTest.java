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
import mech.mania.engine.Pair;

import java.util.HashMap;
import java.util.Map;

public class CropTest {
    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger(0);
    private final static JsonLogger BOT_LOGGER = new JsonLogger(0);
    private final static JsonLogger OPPONENT_LOGGER = new JsonLogger(0);

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

        // type, expected profit, start turn (first turn of growing area is 13)
        Map<CropType, Pair<Double, Integer>> expectedProfitMargin = new HashMap<>();
        expectedProfitMargin.put(CropType.POTATO, new Pair<>(1.10, 1));
        expectedProfitMargin.put(CropType.CORN, new Pair<>(1.25, 24));
        expectedProfitMargin.put(CropType.GRAPE, new Pair<>(1.25, 21));
        expectedProfitMargin.put(CropType.JOGANFRUIT, new Pair<>(1.29, 24));
        expectedProfitMargin.put(CropType.PEANUTS, new Pair<>(1.00, 1));
        expectedProfitMargin.put(CropType.QUADROTRITICALE, new Pair<>(1.36, 21)); // ended with 40.50 after starting at turn 13
        expectedProfitMargin.put(CropType.DUCHAMFRUIT, new Pair<>(1.38, 24));
        expectedProfitMargin.put(CropType.GOLDENCORN, new Pair<>(2.25, 24));

        for (Map.Entry<CropType, Pair<Double, Integer>> entry : expectedProfitMargin.entrySet()) {
            CropType type = entry.getKey();
            double expectedProfit = entry.getValue().getKey();
            int startTurn = entry.getValue().getValue();
            int growthTime = type.getGrowthTime();
            double growthValue = getGrowthValueAfterNTurns(type, growthTime, startTurn, engineLogger);
            System.err.printf("Crop: %s, Original Price: %d, Final Price after %d turns: %.2f\n", type, type.getSeedBuyPrice(), growthTime, growthValue);
            double originalPrice = type.getSeedBuyPrice();
            if (expectedProfit > 0) {
                Assert.assertTrue(growthValue >= originalPrice);
            }
            double margin = growthValue / originalPrice;
            Assert.assertEquals(expectedProfit, margin, 0.1);
        }
    }

    private double getGrowthValueAfterNTurns(CropType type, int turns, int startTurn, JsonLogger engineLogger) throws PlayerDecisionParseException {
        // give player ability to plant
        state.setTurn(startTurn);
        state.getPlayer(MY_PLAYER_ID).addSeeds(type, 1);

        // plant crop
        PlayerDecision player1Decision = new PlantAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player1Decision.parse(String.format("%s 3 3", type));
        PlayerDecision player2Decision = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player2Decision.parse("6 3");  // move to the same position (no action)
        GameState newState = GameLogic.updateGameState(state, player1Decision,
                player2Decision, GAME_CONFIG, engineLogger);
        engineLogger.incrementTurn();

        // check growth value
        Crop plantedCrop = newState.getTileMap().get(new Position(3, 3)).getCrop();
        Assert.assertEquals(type, plantedCrop.getType());
        Assert.assertEquals(0, plantedCrop.getValue(), 1e-3);

        for (int i = 0; i < turns; i++) {
            // advance turn
            player1Decision = new MoveAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
            player1Decision.parse("3 3");  // move to the same position (no action)
            player2Decision = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
            player2Decision.parse("6 3");  // move to the same position (no action)
            double oldvalue = newState.getTileMap().get(new Position(3, 3)).getCrop().getValue();
            newState = GameLogic.updateGameState(newState, player1Decision,
                    player2Decision, GAME_CONFIG, engineLogger);
            engineLogger.incrementTurn();
            double newvalue = newState.getTileMap().get(new Position(3, 3)).getCrop().getValue();
            System.err.printf("Crop: %s, Price after %d turns: %.2f (grew %.2f)\n", type, i + 1, newvalue, newvalue - oldvalue);
        }

        // check growth value
        plantedCrop = newState.getTileMap().get(new Position(3, 3)).getCrop();
        Assert.assertEquals(type, plantedCrop.getType());
        // growth formula: GVT * [(1 * (1 - FS)) + (TF * FS)]
        return plantedCrop.getValue();
    }
}

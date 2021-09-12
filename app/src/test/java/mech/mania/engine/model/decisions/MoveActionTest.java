package mech.mania.engine.model.decisions;

import mech.mania.engine.config.Config;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MoveActionTest {

    private final static int MY_PLAYER_ID = 0;
    private final static String MY_PLAYER_NAME = "bot1";
    private final static int OPPONENT_PLAYER_ID = 1;
    private final static String OPPONENT_PLAYER_NAME = "bot2";

    private final static Config GAME_CONFIG = new Config("debug");
    private final static JsonLogger BOT_LOGGER = new JsonLogger();
    private final static JsonLogger ENGINE_LOGGER = new JsonLogger();

    @Test
    public void moveActionParseDecisionTest() throws PlayerDecisionParseException {
        MoveAction action = new MoveAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);

        String regularDecision = "1 1";
        action.parse(regularDecision);
        Assert.assertEquals(new Position(1, 1), action.destination);

        String negativeMoveDecision = "-1 -1";
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(negativeMoveDecision));

        String bigIntMoveDecision = String.format("%d %d", (long) Integer.MAX_VALUE + 1, (long) Integer.MAX_VALUE + 1);
        Assert.assertThrows(PlayerDecisionParseException.class, () -> action.parse(bigIntMoveDecision));

        // should parse, but is invalid to move to
        String outOfBoundsMoveDecision = String.format("%d %d", GAME_CONFIG.BOARD_WIDTH, GAME_CONFIG.BOARD_HEIGHT);
        action.parse(outOfBoundsMoveDecision);
        Assert.assertEquals(new Position(GAME_CONFIG.BOARD_WIDTH, GAME_CONFIG.BOARD_HEIGHT), action.destination);
    }

    @Test
    public void regularMoveActionPerformActionTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        String player1Decision = "1 1";
        MoveAction player1Action = new MoveAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player1Action.parse(player1Decision);
        player1Action.performAction(state);

        Assert.assertEquals(new Position(1, 1), state.getPlayer(MY_PLAYER_ID).getPosition());

        String player2Decision = String.format("%d %d",
                GAME_CONFIG.BOARD_WIDTH - 2,
                GAME_CONFIG.BOARD_HEIGHT - 2);
        MoveAction player2Action = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player2Action.parse(player2Decision);
        player2Action.performAction(state);

        Assert.assertEquals(new Position(GAME_CONFIG.BOARD_WIDTH - 2, GAME_CONFIG.BOARD_HEIGHT - 2),
                state.getPlayer(OPPONENT_PLAYER_ID).getPosition());
    }

    @Test
    public void farMoveActionPerformActionTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        // BOARD SIZE IS 10x10 (see debug.properties)

        // top left to bottom right
        Position player1Destination = new Position(9, 9);
        String player1Decision = String.format("%d %d", player1Destination.getX(), player1Destination.getY());
        MoveAction player1Action = new MoveAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player1Action.parse(player1Decision);
        player1Action.performAction(state);

        // didn't move, ends at the stating position
        Assert.assertEquals(new Position(0, 0), state.getPlayer(MY_PLAYER_ID).getPosition());

        // fails for the right reason
        List<String> exceptionLogs = ENGINE_LOGGER.getExceptionLogs();
        Assert.assertEquals("Player 1: Failed to move to position (9,9), greater than allowed movement (18 > 10)",
                exceptionLogs.get(exceptionLogs.size() - 1));

        // top right to bottom left
        Position player2Destination = new Position(0, 9);
        String player2Decision = String.format("%d %d", player2Destination.getX(), player2Destination.getY());
        MoveAction player2Action = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        player2Action.parse(player2Decision);
        player2Action.performAction(state);

        // didn't move, ends at the starting position
        Assert.assertEquals(new Position(GAME_CONFIG.BOARD_WIDTH - 1, 0),
                state.getPlayer(OPPONENT_PLAYER_ID).getPosition());

        // fails for the right reason
        exceptionLogs = ENGINE_LOGGER.getExceptionLogs();
        Assert.assertEquals("Player 2: Failed to move to position (0,9), greater than allowed movement (18 > 10)",
                exceptionLogs.get(exceptionLogs.size() - 1));
    }

    @Test
    public void greenGrocerMoveActionPerformActionTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.NONE;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        // make player harvest something that they can sell
        double initialMoney = state.getPlayer(MY_PLAYER_ID).getMoney();
        double additionalMoney = 15;
        Tile tile = new Tile(TileType.F_BAND_INNER);
        tile.setCrop(new Crop(CropType.CORN));
        tile.getCrop().setValue(additionalMoney);
        // this will simply put the crop in the player's inventory
        state.getPlayer(MY_PLAYER_ID).harvest(tile);

        // make sure the crop has been removed from the tile
        Assert.assertEquals(CropType.NONE, tile.getCrop().getType());

        // BOARD SIZE IS 10x10 (see debug.properties)

        // green grocer move (x = 3, 4, 5, 6 are green grocers, see debug.properties)
        Position destination = new Position(4, 0);
        String decision = String.format("%d %d", destination.getX(), destination.getY());
        MoveAction action = new MoveAction(MY_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        action.parse(decision);
        action.performAction(state);

        // didn't move, ends at the stating position
        Assert.assertEquals(new Position(4, 0), state.getPlayer(MY_PLAYER_ID).getPosition());
        Assert.assertEquals(initialMoney + additionalMoney, state.getPlayer(MY_PLAYER_ID).getMoney(), 1e-3);

        // fails for the right reason
        List<String> infoLogs = ENGINE_LOGGER.getInfoLogs();
        Assert.assertEquals("Player 1: Selling inventory",
                infoLogs.get(infoLogs.size() - 1));
    }

    @Test
    public void upgradeMoveDistanceTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.LONGER_LEGS;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(new Position(1, GAME_CONFIG.MAX_MOVEMENT + 2));

        String playerDecision = "1 1";
        MoveAction playerAction = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        playerAction.parse(playerDecision);
        playerAction.performAction(state);

        Assert.assertEquals(new Position(1, 1), state.getPlayer(OPPONENT_PLAYER_ID).getPosition());
    }

    @Test
    public void outsideUpgradeMoveDistanceTest() throws PlayerDecisionParseException {
        ItemType myPlayerItem = ItemType.NONE;
        UpgradeType myPlayerUpgrade = UpgradeType.NONE;
        ItemType opponentPlayerItem = ItemType.NONE;
        UpgradeType opponentPlayerUpgrade = UpgradeType.LONGER_LEGS;

        GameState state = new GameState(GAME_CONFIG, MY_PLAYER_NAME, myPlayerItem, myPlayerUpgrade,
                OPPONENT_PLAYER_NAME, opponentPlayerItem, opponentPlayerUpgrade);

        state.getPlayer(OPPONENT_PLAYER_ID).setPosition(new Position(1, GAME_CONFIG.LONGER_LEGS_MAX_MOVEMENT + 2));

        String playerDecision = "1 1";
        MoveAction playerAction = new MoveAction(OPPONENT_PLAYER_ID, BOT_LOGGER, ENGINE_LOGGER);
        playerAction.parse(playerDecision);
        playerAction.performAction(state);

        Assert.assertNotEquals(new Position(1, 1), state.getPlayer(MY_PLAYER_ID).getPosition());
    }

    // TODO: do we need to test null destinations? this will only occur if we forget to call parse(String)
}

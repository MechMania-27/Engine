package mech.mania.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.Game;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for Main function (gameLoop, writeListToFile, and main).
 */
public class GameTest {

    /**
     * Takes in one integer argument that denotes which turn the bot will crash on
     * (note: working directory is Engine/app)
     */
    private final String CRASHING_BOT_EXEC = "python3 -u src/test/resources/bots/crashing_bot.py";

    private final Config gameConfig;

    public GameTest() {
        gameConfig = new Config("debug");
    }

    private void printBotLogs(JsonLogger player1, JsonLogger player2, JsonLogger engine) {
        System.out.printf(
                "========================= BOT LOGS ==============================\n" +
                "bot1 log: %s\n" +
                "-----------------------------------------------------------------\n" +
                "bot2 log: %s\n" +
                "========================= ENGINE LOG ============================\n" +
                "engine log: %s\n" +
                "=================================================================\n",
            String.join("\n", prettyPrint(player1.serializedString())),
            String.join("\n", prettyPrint(player2.serializedString())),
            String.join("\n", prettyPrint(engine.serializedString())));
    }

    private String prettyPrint(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(JsonParser.parseString(jsonString));
    }

    private GameLog launchCrashingBots(int bot1CrashTurn, int bot2CrashTurn) throws IOException {
        JsonLogger player1Logger = new JsonLogger(0);
        JsonLogger player2Logger = new JsonLogger(0);
        JsonLogger engineLogger = new JsonLogger(0);

        // launch bot 1
        String bot1Executable = CRASHING_BOT_EXEC + " " + bot1CrashTurn;
        PlayerCommunicationInfo bot1 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player1Logger, 0,
                        "bot1", bot1Executable);
        bot1.start();
        bot1.askForStartingItems();

        // launch bot 2
        String bot2Executable = CRASHING_BOT_EXEC + " " + bot2CrashTurn;
        PlayerCommunicationInfo bot2 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player2Logger, 1,
                        "bot2", bot2Executable);
        bot2.start();
        bot2.askForStartingItems();

        player1Logger.incrementTurn();
        player2Logger.incrementTurn();
        engineLogger.incrementTurn();

        Game game = new Game(gameConfig, bot1, bot2, engineLogger);
        GameLog gameLog = game.run();

        bot1.stop();
        bot2.stop();

        printBotLogs(player1Logger, player2Logger, engineLogger);

        return gameLog;
    }

    /**
     * Test to make sure bot2 wins if only bot1 crashes.
     */
    @Test
    public void bot1CrashesBot2Wins() throws IOException {
        GameLog log = launchCrashingBots(5, 0);
        Assert.assertEquals(PlayerEndState.ERROR, log.getPlayer1EndState());
        Assert.assertEquals(PlayerEndState.WON, log.getPlayer2EndState());
    }

    /**
     * Test to make sure both bots have a chance to crash on the same turn for a tie.
     */
    @Test
    public void bothBotsCanCrashResultCrash() throws IOException {
        GameLog log = launchCrashingBots(5, 5);
        Assert.assertEquals(PlayerEndState.ERROR, log.getPlayer1EndState());
        Assert.assertEquals(PlayerEndState.ERROR, log.getPlayer2EndState());
    }

    /**
     * Test to make sure bot5 can crash as well.
     */
    @Test
    public void bot5CrashesBot1Wins() throws IOException {
        GameLog log = launchCrashingBots(0, 5);
        Assert.assertEquals(PlayerEndState.WON, log.getPlayer1EndState());
        Assert.assertEquals(PlayerEndState.ERROR, log.getPlayer2EndState());
    }
}

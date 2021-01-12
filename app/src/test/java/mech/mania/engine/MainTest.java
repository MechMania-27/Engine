package mech.mania.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.PlayerEndState;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static mech.mania.engine.Main.gameLoop;

/**
 * Unit test for Main function (gameLoop, writeListToFile, and main).
 */
public class MainTest {

    /**
     * Takes in one integer argument that denotes which turn the bot will crash on
     * (note: working directory is Engine/app)
     */
    private final String CRASHING_BOT_EXEC = "python3 -u src/test/resources/bots/crashing_bot.py";

    private final Config gameConfig;

    public MainTest() {
        gameConfig = new Config("debug");
    }

    private void printBotLogs(JsonLogger player1, JsonLogger player2) {
        System.out.printf(
                "========================= BOT LOGS ==============================\n" +
                "bot1 log:\n%s\n" +
                "-----------------------------------------------------------------\n" +
                "bot2 log:\n%s\n" +
                "=================================================================%n",
            String.join("\n", prettyPrint(player1.serializedString())),
            String.join("\n", prettyPrint(player2.serializedString())));
    }

    private String prettyPrint(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(JsonParser.parseString(jsonString));
    }

    /**
     * Test to make sure bot2 wins if only bot1 crashes.
     */
    @Test
    public void bot1CrashesBot2Wins() throws IOException {
        JsonLogger player1Logger = new JsonLogger(0);
        JsonLogger player2Logger = new JsonLogger(0);
        JsonLogger engineLogger = new JsonLogger(0);

        // launch bot 1 (crashes before bot 2)
        String bot1Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot1 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player1Logger,
                        "bot1", bot1Executable);
        bot1.start();

        // launch bot 2 (doesn't crash until turn 10)
        String bot2Executable = CRASHING_BOT_EXEC + " 10";
        PlayerCommunicationInfo bot2 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player2Logger,
                        "bot2", bot2Executable);
        bot2.start();

        GameLog gameLog = new GameLog();
        gameLoop(gameConfig, gameLog, bot1, bot2, engineLogger);
        PlayerEndState player1EndState = gameLog.getPlayer1EndState();
        PlayerEndState player2EndState = gameLog.getPlayer2EndState();

        printBotLogs(player1Logger, player2Logger);

        Assert.assertEquals(PlayerEndState.ERROR, player1EndState);
        Assert.assertEquals(PlayerEndState.WON, player2EndState);
    }

    /**
     * Test to make sure both bots have a chance to crash on the same turn for a tie.
     */
    @Test
    public void bothBotsCanCrashResultCrash() throws IOException {
        JsonLogger player1Logger = new JsonLogger(0);
        JsonLogger player2Logger = new JsonLogger(0);
        JsonLogger engineLogger = new JsonLogger(0);

        // launch bot 1
        String bot1Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot1 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player1Logger,
                        "bot1", bot1Executable);
        bot1.start();

        // launch bot 2
        String bot2Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot2 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player2Logger,
                        "bot2", bot2Executable);
        bot2.start();

        GameLog gameLog = new GameLog();
        gameLoop(gameConfig, gameLog, bot1, bot2, engineLogger);
        PlayerEndState player1EndState = gameLog.getPlayer1EndState();
        PlayerEndState player2EndState = gameLog.getPlayer2EndState();

        printBotLogs(player1Logger, player2Logger);

        Assert.assertEquals(PlayerEndState.ERROR, player1EndState);
        Assert.assertEquals(PlayerEndState.ERROR, player2EndState);
    }

    /**
     * Test to make sure bot2 can crash as well.
     */
    @Test
    public void bot2CrashesBot1Wins() throws IOException {
        JsonLogger player1Logger = new JsonLogger(0);
        JsonLogger player2Logger = new JsonLogger(0);
        JsonLogger engineLogger = new JsonLogger(0);

        // launch bot 1
        String bot1Executable = CRASHING_BOT_EXEC + " 10";
        PlayerCommunicationInfo bot1 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player1Logger,
                        "bot1", bot1Executable);
        bot1.start();

        // launch bot 2
        String bot2Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot2 =
                new PlayerCommunicationInfo(gameConfig, engineLogger, player2Logger,
                        "bot2", bot2Executable);
        bot2.start();

        GameLog gameLog = new GameLog();
        gameLoop(gameConfig, gameLog, bot1, bot2, engineLogger);
        PlayerEndState player1EndState = gameLog.getPlayer1EndState();
        PlayerEndState player2EndState = gameLog.getPlayer2EndState();

        printBotLogs(player1Logger, player2Logger);

        Assert.assertEquals(PlayerEndState.WON, player1EndState);
        Assert.assertEquals(PlayerEndState.ERROR, player2EndState);
    }
}

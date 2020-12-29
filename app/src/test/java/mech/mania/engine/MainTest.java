package mech.mania.engine;

import mech.mania.engine.config.Config;
import mech.mania.engine.core.Winner;
import mech.mania.engine.model.GameLog;
import mech.mania.engine.networking.PlayerCommunicationInfo;
import org.apache.commons.configuration2.ex.ConfigurationException;
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

    private final Config gameConfig = new Config();

    public MainTest() throws IOException, ConfigurationException { }

    private void printBotLogs(PlayerCommunicationInfo player1, PlayerCommunicationInfo player2) {
        System.out.println(
                String.format("========================= BOT LOGS ==============================\n" +
                        "%s log:\n%s\n" +
                        "-----------------------------------------------------------------\n" +
                        "%s log:\n%s\n" +
                        "=================================================================",
                player1.getPlayerName(),
                String.join("\n", player1.getLogs()),
                player2.getPlayerName(),
                String.join("\n", player2.getLogs())));
    }

    /**
     * Test to make sure bot2 wins if only bot1 crashes.
     */
    @Test
    public void bot1CrashesBot2Wins() throws IOException {
        // launch bot 1 (crashes before bot 2)
        String bot1Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot1 = new PlayerCommunicationInfo("bot1", bot1Executable);
        bot1.start();

        // launch bot 2 (doesn't crash until turn 10)
        String bot2Executable = CRASHING_BOT_EXEC + " 10";
        PlayerCommunicationInfo bot2 = new PlayerCommunicationInfo("bot2", bot2Executable);
        bot2.start();

        Winner winner = gameLoop(gameConfig, new GameLog(), bot1, bot2);

        printBotLogs(bot1, bot2);

        Assert.assertEquals(Winner.PLAYER2, winner);
    }

    /**
     * Test to make sure both bots have a chance to crash on the same turn for a tie.
     */
    @Test
    public void bothBotsCanCrashResultCrash() throws IOException {
        String bot1Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot1 = new PlayerCommunicationInfo("bot1", bot1Executable);
        bot1.start();

        String bot2Executable = CRASHING_BOT_EXEC + " 3";
        PlayerCommunicationInfo bot2 = new PlayerCommunicationInfo("bot2", bot2Executable);
        bot2.start();

        Winner winner = gameLoop(gameConfig, new GameLog(), bot1, bot2);

        printBotLogs(bot1, bot2);

        Assert.assertEquals(Winner.CRASH, winner);
    }

    /**
     * Test to make sure bot2 can crash as well.
     */
    @Test
    public void bot2CrashesBot1Wins() throws IOException {
        String bot1Executable = CRASHING_BOT_EXEC + " 10";
        PlayerCommunicationInfo bot1 = new PlayerCommunicationInfo("bot1", bot1Executable);
        bot1.start();

        String bot2Executable = CRASHING_BOT_EXEC + " 2";
        PlayerCommunicationInfo bot2 = new PlayerCommunicationInfo("bot2", bot2Executable);
        bot2.start();

        Winner winner = gameLoop(gameConfig, new GameLog(), bot1, bot2);

        printBotLogs(bot1, bot2);

        Assert.assertEquals(Winner.PLAYER1, winner);
    }

}

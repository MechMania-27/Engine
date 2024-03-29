package mech.mania.engine.model;
import mech.mania.engine.config.Config;
import mech.mania.engine.core.GameLogic;
import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.decisions.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AchievementsTest {
    private final Config gameConfig;
    private GameState gameState;
    private final Player player1;
    private final Player player2;
    private final Position greenGrocer;
    private final JsonLogger playerLogger;
    private final JsonLogger engineLogger;
    public AchievementsTest() {
        gameConfig = new Config("debug");
        gameState = new GameState(gameConfig, "p1", ItemType.NONE, UpgradeType.NONE, "p2", ItemType.NONE, UpgradeType.NONE);
        player1 = gameState.getPlayer1();
        player2 = gameState.getPlayer2();
        greenGrocer = gameState.getTileMap().getGreenGrocer().get(0);
        playerLogger = new JsonLogger(0);
        engineLogger = new JsonLogger(0);
    }

    /**
     * Test for the achievement "Not Worth the Dirt He Sows", "A Worthy Heir", "It Ain’t Much, but It’s Honest Work"
     */
    @Test
    public void Achievement1and7and8() throws PlayerDecisionParseException {
        PlayerDecision buyAction = new BuyDecision(0, playerLogger, engineLogger)
                .parse("corn 10");
        PlayerDecision moveToGrocer = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        moveToGrocer.performAction(gameState);
        buyAction.performAction(gameState);
        String achievement1 = "Not Worth the Dirt He Sows";
        String achievement2 = "A Worthy Heir";
        String achievement3 = "It Ain't Much, but It's Honest Work";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertTrue(p1achievements.contains(achievement1));
        Assert.assertFalse(p1achievements.contains(achievement2));
        Assert.assertFalse(p1achievements.contains(achievement3));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertFalse(p2achievements.contains(achievement1));
        Assert.assertTrue(p2achievements.contains(achievement2));
        Assert.assertTrue(p2achievements.contains(achievement3));
    }

    /**
     * Test for the achievement "My Favorite Customer", "Botanical Burglary"
     */
    @Test
    public void achievement2() throws PlayerDecisionParseException {
        PlayerDecision buyAction = new BuyDecision(0, playerLogger, engineLogger)
                .parse("corn 200");
        PlayerDecision moveToGrocer = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        gameState.getPlayer1().setMoney(10000);
        moveToGrocer.performAction(gameState);
        buyAction.performAction(gameState);
        String achievement = "My Favorite Customer";
        String achievement2 = "Botanical Burglary";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertTrue(p1achievements.contains(achievement));
        Assert.assertFalse(p1achievements.contains(achievement2));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertFalse(p2achievements.contains(achievement));
        Assert.assertTrue(p2achievements.contains(achievement2));
    }

    /**
     * Test for the achievement "Dust Bowl"
     */
    @Test
    public void achievement3() throws PlayerDecisionParseException {
        PlayerDecision buyAction = new BuyDecision(0, playerLogger, engineLogger)
                .parse("corn 200");
        PlayerDecision moveToGrocer = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        PlayerDecision player2Decision = new MoveDecision(1, playerLogger, engineLogger)
                .parse("5 5");
        gameState.getPlayer1().setMoney(10000);
        moveToGrocer.performAction(gameState);
        buyAction.performAction(gameState);
        for(int i = 0; i < 10; i++) {
            PlayerDecision moveToTile = new MoveDecision(0, playerLogger, engineLogger)
                    .parse(String.format("%d %d", i, 3));
            moveToTile.performAction(gameState);
            PlayerDecision plant = new PlantDecision(0, playerLogger, engineLogger)
                    .parse(String.format("corn %d %d", i, 3));
            plant.performAction(gameState);
            //gameState = GameLogic.updateGameState(gameState, plant, player2Decision, gameConfig, engineLogger);
        }
        gameState.setTurn(155);
        gameState = GameLogic.updateGameState(gameState, moveToGrocer, player2Decision, gameConfig, engineLogger);
        //Position p = new Position(3, 0);
        //Tile t = gameState.getTileMap().getTile(p);
        //System.out.println(t.getType());
        //System.out.println(t.getPlanter().getMoney());
        //System.out.println(t.getType());
        //System.out.println(player1.getAchievements().getCropsDestroyed());
        String achievement = "Dust Bowl";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertTrue(p1achievements.contains(achievement));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertFalse(p2achievements.contains(achievement));
    }

    /**
     * Test for the achievement "Seedy Business"
     */
    @Test
    public void achievement4() throws PlayerDecisionParseException {
        int x = 3;
        int y = 4;
        PlayerDecision buyAction = new BuyDecision(0, playerLogger, engineLogger)
                .parse("corn 200");
        PlayerDecision moveToGrocer = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        gameState.getPlayer1().setMoney(10000);
        moveToGrocer.performAction(gameState);
        buyAction.performAction(gameState);
        PlayerDecision moveToTile = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", x, y));
        moveToTile.performAction(gameState);
        PlayerDecision plant = new PlantDecision(0, playerLogger, engineLogger)
                .parse(String.format("corn %d %d", x, y));
        plant.performAction(gameState);
        Position p = new Position(x, y);
        gameState.getTileMap().get(p).getCrop().setGrowthTimer(0);
        player2.setPosition(new Position(x, y + 1));
        player1.setPosition(new Position(9,9));
        PlayerDecision harvest = new HarvestDecision(1, playerLogger, engineLogger)
                .parse(String.format("%d %d", x, y));
        harvest.performAction(gameState);
        PlayerDecision moveToGrocer2 = new MoveDecision(1, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        moveToGrocer2.performAction(gameState);
        player2.sellInventory();


        String achievement = "Seedy Business";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertFalse(p1achievements.contains(achievement));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertTrue(p2achievements.contains(achievement));
    }

    /**
     * Test for the achievement "Omni-Agriculturalist"
     */
    @Test
    public void achievement5() {
        player1.addToHarvestInventory(new Crop(CropType.DUCHAM_FRUIT, gameConfig));
        player1.addToHarvestInventory(new Crop(CropType.GRAPE, gameConfig));
        player1.addToHarvestInventory(new Crop(CropType.POTATO, gameConfig));
        player1.addToHarvestInventory(new Crop(CropType.JOGAN_FRUIT, gameConfig));
        player1.addToHarvestInventory(new Crop(CropType.QUADROTRITICALE, gameConfig));
        player1.addToHarvestInventory(new Crop(CropType.CORN, gameConfig));
        player1.sellInventory();
        String achievement = "Omni-Agriculturalist";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertTrue(p1achievements.contains(achievement));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertFalse(p2achievements.contains(achievement));
    }

    /**
     * Test for the achievement "Grapes of Mild Displeasure", "Richer than Phineas Himself", "Fruits of our Labor"
     */
    @Test
    public void achievement6and9and13() throws PlayerDecisionParseException {
        PlayerDecision buyAction = new BuyDecision(0, playerLogger, engineLogger)
                .parse("grape 200");
        PlayerDecision buyAction2 = new BuyDecision(0, playerLogger, engineLogger)
                .parse("corn 1");
        PlayerDecision plantCorn = new PlantDecision(0, playerLogger, engineLogger)
                .parse("corn 2 4");
        PlayerDecision moveToGrocer = new MoveDecision(0, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        gameState.getPlayer1().setMoney(10000);
        moveToGrocer.performAction(gameState);
        buyAction.performAction(gameState);
        buyAction2.performAction(gameState);
        player1.setPosition(new Position(2,4));
        plantCorn.performAction(gameState);

        for(int i = 0; i < 5; i++) {
            PlayerDecision moveToTile = new MoveDecision(0, playerLogger, engineLogger)
                    .parse(String.format("%d %d", i, 3));
            moveToTile.performAction(gameState);
            PlayerDecision plant = new PlantDecision(0, playerLogger, engineLogger)
                    .parse(String.format("grape %d %d", i, 3));
            plant.performAction(gameState);
            Position p = new Position(i, 3);
            gameState.getTileMap().get(p).getCrop().setGrowthTimer(0);
        }
        player1.setPosition(new Position(9,9));
        for(int i = 0; i < 5; i++) {
            player2.setPosition(new Position(i, 3));
            PlayerDecision harvest = new HarvestDecision(1, playerLogger, engineLogger)
                    .parse(String.format("grape %d %d", i, 3));
            harvest.performAction(gameState);
        }

        PlayerDecision moveToGrocer2 = new MoveDecision(1, playerLogger, engineLogger)
                .parse(String.format("%d %d", greenGrocer.getX(), greenGrocer.getY()));
        moveToGrocer2.performAction(gameState);
        player2.sellInventory();


        String achievement = "Grapes of Mild Displeasure";
        String achievement2 = "Richer than Phineas Himself";
        String achievement3 = "Fruits of our Labor";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertFalse(p1achievements.contains(achievement));
        Assert.assertTrue(p1achievements.contains(achievement2));
        Assert.assertFalse(p1achievements.contains(achievement3));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertTrue(p2achievements.contains(achievement));
        Assert.assertFalse(p2achievements.contains(achievement2));
        Assert.assertTrue(p2achievements.contains(achievement3));
    }

    /**
     * Test for the achievement "Ornithophobia"
     */
    @Test
    public void achievement10() throws PlayerDecisionParseException {
        player1.setItem(ItemType.SCARECROW);
        player2.setItem(ItemType.SCARECROW);
        Position p = new Position(3, 3);
        player1.setPosition(p);
        player2.setPosition(p);
        PlayerDecision useItem1 = new UseItemDecision(0, playerLogger, engineLogger)
                .parse("scarecrow 3 3");
        useItem1.performAction(gameState);
        TileMap map= gameState.getTileMap();
        //System.out.println(map.get(3, 3).isScarecrowEffect());
        PlayerDecision useItem2 = new UseItemDecision(1, playerLogger, engineLogger)
                .parse("scarecrow 3 3");
        useItem2.performAction(gameState);
        //System.out.println(map.get(3, 3).isScarecrowEffect());
        String achievement = "Ornithophobia";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertFalse(p1achievements.contains(achievement));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertTrue(p2achievements.contains(achievement));
    }

    /**
     * Test for the achievement "Stalks and Bonds"
     */
    @Test
    public void achievement11() {
        player1.addToHarvestInventory(new Crop(CropType.GOLDEN_CORN, gameConfig));
        player1.sellInventory();
        String achievement = "Stalks and Bonds";
        List<String> p1achievements = player1.getAchievements().getFinalAchievements(false, gameConfig.STARTING_MONEY, player1.getMoney());
        Assert.assertTrue(p1achievements.contains(achievement));
        List<String> p2achievements = player2.getAchievements().getFinalAchievements(true, gameConfig.STARTING_MONEY, player2.getMoney());
        Assert.assertFalse(p2achievements.contains(achievement));
    }



}


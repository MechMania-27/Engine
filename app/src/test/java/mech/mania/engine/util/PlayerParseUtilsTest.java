package mech.mania.engine.util;

import mech.mania.engine.model.*;
import org.junit.Assert;
import org.junit.Test;

public class PlayerParseUtilsTest {
    @Test
    public void itemFromStringTest() {
        String itemString = "";
        Assert.assertEquals(ItemType.NONE, PlayerParseUtils.itemFromString(itemString));

        itemString = null;
        Assert.assertEquals(ItemType.NONE, PlayerParseUtils.itemFromString(itemString));

        itemString = "FERTILITY_IDOL";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertility-idol";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertilityidol";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));

        itemString = "rain_totem";
        Assert.assertEquals(ItemType.RAIN_TOTEM, PlayerParseUtils.itemFromString(itemString));

        itemString = "pesticide";
        Assert.assertEquals(ItemType.PESTICIDE, PlayerParseUtils.itemFromString(itemString));

        itemString = "scarecrow";
        Assert.assertEquals(ItemType.SCARECROW, PlayerParseUtils.itemFromString(itemString));
    }

    @Test
    public void upgradeFromStringTest() {
        String upgradeString = "";
        Assert.assertEquals(UpgradeType.NONE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = null;
        Assert.assertEquals(UpgradeType.NONE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "BIGGER_MUSCLES";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "bigger-muscles";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "biggermuscles";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "longer_legs";
        Assert.assertEquals(UpgradeType.LONGER_LEGS, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "longerscythe";
        Assert.assertEquals(UpgradeType.LONGER_SCYTHE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "rabbits-foot";
        Assert.assertEquals(UpgradeType.RABBITS_FOOT, PlayerParseUtils.upgradeFromString(upgradeString));
    }

    // @Test
    // public void gameStateToStringTest() {
    //     Config debugConfig = new Config("debug");
    //     GameState gameState = new GameState(debugConfig,
    //             "bot1", ItemType.NONE, UpgradeType.NONE,
    //             "bot2", ItemType.NONE, UpgradeType.NONE);

    //     // only fields marked with @Expose will be serialized
    //     String expected = "{\"players\":" +
    //             "[{\"name\":\"bot1\",\"position\":{\"x\":0,\"y\":0},\"item\":\"NONE\",\"upgrade\":\"NONE\",\"money\":0}," +
    //             "{\"name\":\"bot2\",\"position\":{\"x\":1,\"y\":0},\"item\":\"NONE\",\"upgrade\":\"NONE\",\"money\":0}]," +
    //             "\"tileMap\":{\"mapHeight\":2,\"mapWidth\":2," +
    //             "\"tiles\":[[{\"type\":\"SOIL\",\"fertility\":0},{\"type\":\"SOIL\",\"fertility\":0}]," +
    //             "[{\"type\":\"SOIL\",\"fertility\":0},{\"type\":\"SOIL\",\"fertility\":0}]]" +
    //             "}}";
    //     Assert.assertEquals(expected, PlayerParseUtils.sendInfoFromGameState(gameState));
    // }

    // TODO: Re-implement test once decision parsing is working
//    @Test
//    public void parseDecisionTest() {
//        PlayerDecision decision = new PlayerDecision();
//        String decisionString = "move 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(new Position(1, 1), decision.getMovePos());
//
//        decision = new PlayerDecision();
//        decisionString = "plant grape 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.PLANT, decision.getAction());
//        Assert.assertEquals(CropType.GRAPE, decision.getCrops().get(0).getType());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//
//        decision = new PlayerDecision();
//        decisionString = "harvest 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.HARVEST, decision.getAction());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//
//        decision = new PlayerDecision();
//        decisionString = "buy grape 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.BUY, decision.getAction());
//        Assert.assertEquals(CropType.GRAPE, decision.getCrops().get(0).getType());
//        Assert.assertEquals(1, (int) decision.getBuyAmounts().get(0));
//
//        decision = new PlayerDecision();
//        decisionString = "sell";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.SELL, decision.getAction());
//
//        decision = new PlayerDecision();
//        decisionString = "useitem fertilityidol 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.USE_ITEM, decision.getAction());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//
//        decision = new PlayerDecision();
//        decisionString = "use item fertilityidol 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.USE_ITEM, decision.getAction());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//
//        decision = new PlayerDecision();
//        decisionString = "use_item fertilityidol 1 1";
//        PlayerParseUtils.parseDecision(decision, decisionString);
//        Assert.assertEquals(PlayerDecision.ActionType.USE_ITEM, decision.getAction());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//    }

    // TODO: Re-implement test once decision parsing is working
//    @Test
//    public void decisionFromString() throws PlayerDecisionParseException {
//        List<String> decisionStrings = new ArrayList<>();
//        decisionStrings.add("move 1 1");
//        decisionStrings.add("useitem fertility_idol 1 1");
//        // should be invalidated
//        decisionStrings.add("plant potato 1 1");
//        PlayerDecision decision = PlayerParseUtils.decisionFromString(decisionStrings);
//
//        Assert.assertEquals(new Position(1, 1), decision.getMovePos());
//        Assert.assertEquals(PlayerDecision.ActionType.USE_ITEM, decision.getAction());
//        Assert.assertEquals(new Position(1, 1), decision.getActionPositions().get(0));
//        Assert.assertNull(decision.getCrops());
//    }

    @Test
    public void parsePlantTypeTest() {
        Assert.assertEquals(CropType.NONE, PlayerParseUtils.plantTypeFromString(""));
    }

    @Test
    public void parseItemTest() {
        Assert.assertEquals(ItemType.NONE, PlayerParseUtils.itemFromString(""));
    }
}

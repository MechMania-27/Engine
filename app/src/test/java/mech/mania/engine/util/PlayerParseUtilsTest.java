package mech.mania.engine.util;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.Item;
import mech.mania.engine.model.Upgrade;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class PlayerParseUtilsTest {
    @Test
    public void itemFromStringTest() {
        String itemString = "";
        Assert.assertEquals(Item.NONE, PlayerParseUtils.itemFromString(itemString));

        itemString = null;
        Assert.assertEquals(Item.NONE, PlayerParseUtils.itemFromString(itemString));

        itemString = "FERTILITY_IDOL";
        Assert.assertEquals(Item.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertility idol";
        Assert.assertEquals(Item.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertIlity iDol";
        Assert.assertEquals(Item.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertility-idol";
        Assert.assertEquals(Item.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));
        itemString = "fertilityidol";
        Assert.assertEquals(Item.FERTILITY_IDOL, PlayerParseUtils.itemFromString(itemString));

        itemString = "rain totem";
        Assert.assertEquals(Item.RAIN_TOTEM, PlayerParseUtils.itemFromString(itemString));

        itemString = "pesticide";
        Assert.assertEquals(Item.PESTICIDE, PlayerParseUtils.itemFromString(itemString));

        itemString = "scarecrow";
        Assert.assertEquals(Item.SCARECROW, PlayerParseUtils.itemFromString(itemString));
    }

    @Test
    public void upgradeFromStringTest() {
        String upgradeString = "";
        Assert.assertEquals(Upgrade.NONE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = null;
        Assert.assertEquals(Upgrade.NONE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "BIGGER_MUSCLES";
        Assert.assertEquals(Upgrade.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "bigger muscles";
        Assert.assertEquals(Upgrade.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "biGger Muscles";
        Assert.assertEquals(Upgrade.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "bigger-muscles";
        Assert.assertEquals(Upgrade.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));
        upgradeString = "biggermuscles";
        Assert.assertEquals(Upgrade.BIGGER_MUSCLES, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "longer legs";
        Assert.assertEquals(Upgrade.LONGER_LEGS, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "longerscythe";
        Assert.assertEquals(Upgrade.LONGER_SCYTHE, PlayerParseUtils.upgradeFromString(upgradeString));

        upgradeString = "rabbits-foot";
        Assert.assertEquals(Upgrade.RABBITS_FOOT, PlayerParseUtils.upgradeFromString(upgradeString));
    }

    @Test
    public void gameStateToStringTest() throws ConfigurationException {
        Config debugConfig = new Config("debug.xml");
        GameState gameState = new GameState(debugConfig,
                "bot1", Item.NONE, Upgrade.NONE,
                "bot2", Item.NONE, Upgrade.NONE);

        // only fields marked with @Expose will be serialized
        String expected = "{\"players\":" +
                "[{\"name\":\"bot1\",\"position\":{\"x\":0,\"y\":0},\"item\":\"NONE\",\"upgrade\":\"NONE\",\"money\":0}," +
                "{\"name\":\"bot2\",\"position\":{\"x\":1,\"y\":0},\"item\":\"NONE\",\"upgrade\":\"NONE\",\"money\":0}]," +
                "\"tileMap\":{\"mapHeight\":2,\"mapWidth\":2," +
                "\"tiles\":[[{\"type\":\"SOIL\",\"fertility\":0},{\"type\":\"SOIL\",\"fertility\":0}]," +
                "[{\"type\":\"SOIL\",\"fertility\":0},{\"type\":\"SOIL\",\"fertility\":0}]]" +
                "}}";
        Assert.assertEquals(expected, PlayerParseUtils.sendInfoFromGameState(gameState));
    }

    @Test
    public void decisionToStringTest() {
        String decision = "move 1 1";
        // Assert.assertEquals(, PlayerParseUtils.decisionFromString(decision));
    }
}

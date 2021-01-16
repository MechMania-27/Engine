package mech.mania.engine.util;

import mech.mania.engine.model.CropType;
import mech.mania.engine.model.ItemType;
import mech.mania.engine.model.PlayerDecisionParseException;
import mech.mania.engine.model.UpgradeType;
import mech.mania.engine.model.decisions.*;
import org.junit.Assert;
import org.junit.Test;

public class PlayerCommunicationUtilsTest {
    @Test
    public void itemFromStringTest() {
        String itemString = "";
        Assert.assertEquals(ItemType.NONE, PlayerCommunicationUtils.itemFromString(itemString));

        itemString = null;
        Assert.assertEquals(ItemType.NONE, PlayerCommunicationUtils.itemFromString(itemString));

        itemString = "FERTILITY_IDOL";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerCommunicationUtils.itemFromString(itemString));
        itemString = "fertility-idol";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerCommunicationUtils.itemFromString(itemString));
        itemString = "fertilityidol";
        Assert.assertEquals(ItemType.FERTILITY_IDOL, PlayerCommunicationUtils.itemFromString(itemString));

        itemString = "rain_totem";
        Assert.assertEquals(ItemType.RAIN_TOTEM, PlayerCommunicationUtils.itemFromString(itemString));

        itemString = "pesticide";
        Assert.assertEquals(ItemType.PESTICIDE, PlayerCommunicationUtils.itemFromString(itemString));

        itemString = "scarecrow";
        Assert.assertEquals(ItemType.SCARECROW, PlayerCommunicationUtils.itemFromString(itemString));
    }

    @Test
    public void upgradeFromStringTest() {
        String upgradeString = "";
        Assert.assertEquals(UpgradeType.NONE, PlayerCommunicationUtils.upgradeFromString(upgradeString));

        upgradeString = null;
        Assert.assertEquals(UpgradeType.NONE, PlayerCommunicationUtils.upgradeFromString(upgradeString));

        upgradeString = "BIGGER_MUSCLES";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerCommunicationUtils.upgradeFromString(upgradeString));
        upgradeString = "bigger-muscles";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerCommunicationUtils.upgradeFromString(upgradeString));
        upgradeString = "biggermuscles";
        Assert.assertEquals(UpgradeType.BIGGER_MUSCLES, PlayerCommunicationUtils.upgradeFromString(upgradeString));

        upgradeString = "longer_legs";
        Assert.assertEquals(UpgradeType.LONGER_LEGS, PlayerCommunicationUtils.upgradeFromString(upgradeString));

        upgradeString = "longerscythe";
        Assert.assertEquals(UpgradeType.LONGER_SCYTHE, PlayerCommunicationUtils.upgradeFromString(upgradeString));

        upgradeString = "rabbits-foot";
        Assert.assertEquals(UpgradeType.RABBITS_FOOT, PlayerCommunicationUtils.upgradeFromString(upgradeString));
    }

    @Test
    public void parseDecisionTest() throws PlayerDecisionParseException {
        // action specific commands will be tested in each individual ActionTest

        // TODO: add crashing tests

        String decisionString = "buy corn 1";
        PlayerDecision decision = PlayerCommunicationUtils.parseDecision(1, decisionString);
        Assert.assertTrue(decision instanceof BuyAction);

        decisionString = "harvest corn 1 1";
        decision = PlayerCommunicationUtils.parseDecision(1, decisionString);
        Assert.assertTrue(decision instanceof HarvestAction);

        decisionString = "move 1 1";
        decision = PlayerCommunicationUtils.parseDecision(1, decisionString);
        Assert.assertTrue(decision instanceof MoveAction);

        decisionString = "plant corn 1 1";
        decision = PlayerCommunicationUtils.parseDecision(1, decisionString);
        Assert.assertTrue(decision instanceof PlantAction);

        decisionString = "use_item scarecrow 1 1";
        decision = PlayerCommunicationUtils.parseDecision(1, decisionString);
        Assert.assertTrue(decision instanceof UseItemAction);
    }

    @Test
    public void parsePlantTypeTest() {
        Assert.assertEquals(CropType.NONE, PlayerCommunicationUtils.plantTypeFromString(""));
    }

    @Test
    public void parseItemTest() {
        Assert.assertEquals(ItemType.NONE, PlayerCommunicationUtils.itemFromString(""));
    }
}

package mech.mania.engine.util;

import mech.mania.engine.model.Position;
import org.junit.Assert;
import org.junit.Test;

public class GameUtilsTest {
    /**
     * Mostly a sanity check, but just to make sure
     */
    @Test
    public void manhattanDistanceTest() {
        Position pos1 = new Position(1, 1);
        Position pos2 = new Position(5, 4);
        Assert.assertEquals(7, GameUtils.distance(pos1, pos2));
    }
}

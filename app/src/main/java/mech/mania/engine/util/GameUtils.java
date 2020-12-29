package mech.mania.engine.util;

import mech.mania.engine.model.Position;

public class GameUtils {
    /**
     * Manhattan distance
     * @param pos1
     * @param pos2
     * @return Manhattan distance
     */
    public static int distance(Position pos1, Position pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
    }
}

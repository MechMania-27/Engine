package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Position {

    @Expose
    private int x;
    @Expose
    private int y;

    public Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

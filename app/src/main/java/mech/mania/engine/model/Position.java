package mech.mania.engine.model;

public class Position {

    private int x;
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

    public void setPosition(final int x, final int y) {
        // TODO: figure out how to get config here so that we can check validity
    }
}

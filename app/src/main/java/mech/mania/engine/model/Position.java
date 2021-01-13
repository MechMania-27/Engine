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

    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Position)) {
            return false;
        }

        final Position other = (Position) obj;
        return other.x == this.x && other.y == this.y;
    }
}

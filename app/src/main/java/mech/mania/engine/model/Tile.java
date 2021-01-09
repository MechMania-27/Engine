package mech.mania.engine.model;

import com.google.gson.annotations.Expose;

public class Tile {
    @Expose
    private TileType type;
    @Expose
    private int fertility;

    public Tile(TileType type, int fertility) {
        this.type = type;
        this.fertility = fertility;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public int getFertility() {
        return fertility;
    }

    public void setFertility(int fertility) {
        this.fertility = fertility;
    }
}

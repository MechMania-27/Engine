package mech.mania.engine.model;

import mech.mania.engine.config.Config;

public abstract class Upgrade {
    protected Config config;
    public Upgrade(Config config) {
        this.config = config;
    }
    public abstract void applyUpgrade(Player player);
}

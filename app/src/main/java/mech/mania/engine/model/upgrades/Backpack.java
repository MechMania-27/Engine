package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class Backpack extends Upgrade {

    public Backpack(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player) {
        player.setCarryingCapacity(config.BACKPACK_CARRYING_CAPACITY);
    }
}

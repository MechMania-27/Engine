package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class LongerScythe extends Upgrade {

    public LongerScythe(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player) {
        player.setHarvestRadius(config.LONGER_SCYTHE_HARVEST_RADIUS);
    }
}

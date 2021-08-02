package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class Spyglass extends Upgrade {

    public Spyglass(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player) {
        player.setProtectionRadius(config.SPYGLASS_PROTECTION_RADIUS);
    }
}

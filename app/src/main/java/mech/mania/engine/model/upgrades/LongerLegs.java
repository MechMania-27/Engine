package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class LongerLegs extends Upgrade {

    public LongerLegs(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player){
        player.setMaxMovement(config.LONGER_LEGS_MAX_MOVEMENT);
    }
}

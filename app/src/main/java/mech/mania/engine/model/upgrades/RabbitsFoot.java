package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class RabbitsFoot extends Upgrade {
    public RabbitsFoot(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player){
        player.setDoubleDropChance(config.RABBITS_FOOT_DOUBLE_DROP_CHANCE);
        // TODO: will upgrade drop more than double ever?
    }
}

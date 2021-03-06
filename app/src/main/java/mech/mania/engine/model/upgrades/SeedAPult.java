package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class SeedAPult extends Upgrade {

    public SeedAPult(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player) {
        player.setPlantRadius(config.SEED_A_PULT_PLANT_RADIUS);
    }
}

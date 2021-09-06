package mech.mania.engine.model.upgrades;

import mech.mania.engine.config.Config;
import mech.mania.engine.model.Player;
import mech.mania.engine.model.Upgrade;

public class GreenGrocerLoyaltyCard extends Upgrade {

    public GreenGrocerLoyaltyCard(Config config) {
        super(config);
    }

    @Override
    public void applyUpgrade(Player player){
        if (player.getAmountSpent() > 25) {
            player.setDiscount(config.GREEN_GROCER_LOYALTY_CARD_DISCOUNT);
        } else {
            player.setDiscount(0);
        }
    }
}

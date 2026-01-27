package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.DecimateEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class IronScimitarItem extends Item {
    public IronScimitarItem(GameMember owner) {
        super(owner, 1275, "Iron Scimitar");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.BRONZE_CUTLASS, Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new DecimateEffect(getOwner(), 4, .8f)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 50)
                .put(StatType.CRIT_CHANCE, .4f);
    }

    @Override
    public String getTip() {
        return "Crits apply Cripple";
    }
}

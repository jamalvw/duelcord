package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class BloodlustBladeItem extends Item {
    public static final int COST = 550;

    public BloodlustBladeItem(GameMember owner) {
        super(owner, COST, "Bloodlust Blade");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KNIFE};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 20)
                .put(StatType.LIFE_STEAL, 0.07f);
    }
}

package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.BloodWellEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class SoulstealerItem extends Item {
    public SoulstealerItem(GameMember owner) {
        super(owner, 1200, "Soulstealer");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.BLOODLUST_BLADE, Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new BloodWellEffect(getOwner(), .2f, 50)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 55)
                .put(StatType.LIFE_STEAL, .2f);
    }

    @Override
    public String getTip() {
        return "Attacks shield";
    }
}

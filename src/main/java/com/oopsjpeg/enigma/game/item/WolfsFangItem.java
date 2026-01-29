package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.LifewasterEffect;
import com.oopsjpeg.enigma.game.effect.WolfbiteEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class WolfsFangItem extends Item {
    public WolfsFangItem(GameMember owner) {
        super(owner, 1200, "Wolf's Fang");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.BONE_SPEAR, Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{
                new LifewasterEffect(getOwner(), 3, .6f),
                new WolfbiteEffect(getOwner(), 35)
        };
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 40);
    }

    @Override
    public String getTip() {
        return "Damage to Healers";
    }
}

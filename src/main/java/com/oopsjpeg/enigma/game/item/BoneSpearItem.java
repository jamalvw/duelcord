package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.LifewasterEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;

public class BoneSpearItem extends Item {
    public BoneSpearItem(GameMember owner) {
        super(owner, 575, "Bone Spear");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new LifewasterEffect(getOwner(), 5, 0.75f)};
    }

    @Override
    public Stats getStats() {
        return new Stats().put(ATTACK_POWER, 15);
    }

    @Override
    public String getTip() {
        return "Lower enemy healing";
    }
}

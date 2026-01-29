package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.MagicalMasteryEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class CrimsonMightItem extends Item {
    public CrimsonMightItem(GameMember owner) {
        super(owner, 1175, "Crimson Might");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KORAS_AMULET, Items.RING};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new MagicalMasteryEffect(getOwner(), 1, 2, 0.03f)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 60);
    }

    @Override
    public String getTip() {
        return "Lower cooldowns";
    }
}

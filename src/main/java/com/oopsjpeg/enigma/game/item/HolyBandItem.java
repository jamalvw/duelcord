package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.DivinityEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class HolyBandItem extends Item {
    public HolyBandItem(GameMember owner) {
        super(owner, 550, "Holy Band");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.RING};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new DivinityEffect(getOwner(), 25)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 30);
    }

    @Override
    public String getTip() {
        return "Shield when defending";
    }
}

package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.KorasWillEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class FaithbreakerItem extends Item {
    public FaithbreakerItem(GameMember owner) {
        super(owner, 1150, "Faithbreaker");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KORAS_AMULET, Items.RING};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new KorasWillEffect(getOwner(), 20, .4f)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 55);
    }

    @Override
    public String getDescription() {
        return "Thus, their instruments of faith became the weapons of Gods.";
    }

    @Override
    public String getTip() {
        return "More skill damage";
    }
}

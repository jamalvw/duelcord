package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.KorasWillEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class KorasAmuletItem extends Item {
    public KorasAmuletItem(GameMember owner) {
        super(owner, 525, "Kora's Amulet");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.RING};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new KorasWillEffect(getOwner(), 10)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 20);
    }

    @Override
    public String getTip() {
        return "More skill damage";
    }
}

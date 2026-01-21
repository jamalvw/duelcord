package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.EndlessStrikesEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class MidnightDaggerItem extends Item {
    public MidnightDaggerItem(GameMember owner) {
        super(owner, 625, "Midnight Dagger");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new EndlessStrikesEffect(getOwner(), .12f)};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 15);
    }

    @Override
    public String getTip() {
        return "More multi-attack damage";
    }
}

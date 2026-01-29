package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.DivinityEffect;
import com.oopsjpeg.enigma.game.effect.RestingFaithEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class DawnHammerItem extends Item {
    public DawnHammerItem(GameMember owner) {
        super(owner, 1200, "Dawn Hammer");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.HOLY_BAND, Items.KNIFE};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{
                new DivinityEffect(getOwner(), 30, .55f),
                new RestingFaithEffect(getOwner(), 50)
        };
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 25)
                .put(StatType.SKILL_POWER, 55);
    }

    @Override
    public String getTip() {
        return "Bonus energy";
    }
}

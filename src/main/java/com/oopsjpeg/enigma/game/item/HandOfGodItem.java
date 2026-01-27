package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.GodsMightEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class HandOfGodItem extends Item {
    public HandOfGodItem(GameMember owner) {
        super(owner, 1275, "Hand of God");
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 45)
                .put(StatType.CRIT_CHANCE, 0.4f);
    }

    @Override
    public String getDescription() {
        return "\"And with this, may the realms be reforged in your image.\"";
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new GodsMightEffect(getOwner())};
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.SPIRIT_GAUNTLET, Items.RING};
    }

    @Override
    public String getTip() {
        return "Skills can crit";
    }
}

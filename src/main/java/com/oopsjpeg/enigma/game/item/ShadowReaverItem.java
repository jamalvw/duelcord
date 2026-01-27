package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.effect.EndlessStrikesEffect;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class ShadowReaverItem extends Item {
    public ShadowReaverItem(GameMember owner) {
        super(owner, 1250, "Shadow Reaver");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.MIDNIGHT_DAGGER, Items.RING};
    }

    @Override
    public Effect[] generateEffects() {
        return new Effect[]{new EndlessStrikesEffect(getOwner(), .2f)};
    }

    @Override
    public String getDescription() {
        return "The people spoke of his peaceful posture—well mannered, and foolhardy. But night must always fall.";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 40)
                .put(StatType.SKILL_POWER, 40);
    }

    @Override
    public String getTip() {
        return "More multi-attack damage";
    }
}

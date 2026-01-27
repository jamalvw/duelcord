package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class SpiritGauntletItem extends Item {
    public SpiritGauntletItem(GameMember owner) {
        super(owner, 550, "Spirit Gauntlet");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.RING};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.SKILL_POWER, 25)
                .put(StatType.CRIT_CHANCE, 0.2f);
    }
}

package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;

public class BronzeCutlassItem extends Item {
    public BronzeCutlassItem(GameMember owner) {
        super(owner, 600, "Bronze Cutlass");
    }

    @Override
    public Items[] getBuild() {
        return new Items[]{Items.KNIFE};
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.ATTACK_POWER, 20)
                .put(StatType.CRIT_CHANCE, .2f);
    }
}

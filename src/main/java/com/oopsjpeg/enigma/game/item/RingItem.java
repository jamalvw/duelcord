package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Item;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;

public class RingItem extends Item {
    public RingItem(GameMember owner) {
        super(owner, 200, "Ring");
    }

    @Override
    public Stats getStats() {
        return new Stats().put(SKILL_POWER, 10);
    }
}

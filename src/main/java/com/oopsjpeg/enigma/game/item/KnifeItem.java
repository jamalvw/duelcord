package com.oopsjpeg.enigma.game.item;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Item;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;

public class KnifeItem extends Item {
    public KnifeItem(GameMember owner) {
        super(owner, 250, "Knife");
    }

    @Override
    public Stats getStats() {
        return new Stats().put(ATTACK_POWER, 10);
    }
}

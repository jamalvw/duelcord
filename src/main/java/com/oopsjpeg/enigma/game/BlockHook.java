package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;

import static com.oopsjpeg.enigma.util.Util.RANDOM;

public class BlockHook implements DamageHook {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.RESISTANCE;
    }

    @Override
    public void execute(DamageEvent event) {
        if (!event.isAttack()) return;

        float rand = RANDOM.nextFloat();

        if (rand < event.getVictim().getStats().get(StatType.BLOCK_CHANCE))
        {
            event.multiplyDamage(0.15f);
            event.setBlocked(true);
        }
    }
}

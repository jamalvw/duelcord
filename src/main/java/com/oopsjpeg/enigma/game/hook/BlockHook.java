package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.event.DamageEvent;

import static com.oopsjpeg.enigma.util.Util.RANDOM;

public class BlockHook extends Hook<DamageEvent> {
    public BlockHook() {
        super(EventType.DAMAGE_ALL, Priority.VALIDATION, event -> {
            if (!event.isAttack()) return;

            float rand = RANDOM.nextFloat();

            if (event.isBlocked() || rand < event.getVictim().getStats().get(StatType.BLOCK_CHANCE)) {
                event.multiplyDamage(0.15f);
                event.setBlocked(true);
            }
        });
    }
}

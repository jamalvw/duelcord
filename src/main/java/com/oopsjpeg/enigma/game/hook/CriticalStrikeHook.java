package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.util.Pity;

import static com.oopsjpeg.enigma.game.StatType.CRIT_DAMAGE;

public class CriticalStrikeHook extends Hook<DamageEvent> {
    public CriticalStrikeHook() {
        super(EventType.DAMAGE_ALL, Priority.PRE_CALCULATION, event -> {
            if (event.isAbleToCrit()) {
                Pity pity = event.getActor().getCritPity();

                if (event.isGoingToCrit() || pity.roll()) {
                    event.crit();
                    event.multiplyDamage(1.5f + event.getActor().getStats().get(CRIT_DAMAGE));
                }
            }
        });
    }
}

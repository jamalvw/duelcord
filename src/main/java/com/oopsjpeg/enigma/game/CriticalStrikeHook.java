package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.util.Pity;

import static com.oopsjpeg.enigma.game.StatType.CRIT_DAMAGE;

public class CriticalStrikeHook implements Hook<DamageEvent> {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.PRE_CALCULATION;
    }

    @Override
    public void execute(final DamageEvent event) {
        if (event.isAbleToCrit()) {
            Pity pity = event.getActor().getCritPity();

            if (event.isGoingToCrit() || pity.roll()) {
                event.crit();
                event.multiplyDamage(1.5f + event.getActor().getStats().get(CRIT_DAMAGE));
            }
        }
    }
}

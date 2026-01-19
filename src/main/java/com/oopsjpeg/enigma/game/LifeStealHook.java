package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;

public class LifeStealHook implements DamageHook {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.POST_DAMAGE;
    }

    @Override
    public void execute(DamageEvent event) {
        if (event.isOnHit())
        {
            GameMember attacker = event.getAttacker();
            float heal = event.getDamage() * attacker.getStats().get(StatType.LIFE_STEAL);
            event.addHealing(heal * event.getOnHitScale());
        }
    }
}

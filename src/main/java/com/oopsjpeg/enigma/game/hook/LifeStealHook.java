package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.event.DamageEvent;

public class LifeStealHook extends Hook<DamageEvent> {
    public LifeStealHook() {
        super(EventType.DAMAGE_ALL, Priority.POST_DAMAGE, event -> {
            if (event.isOnHit()) {
                GameMember attacker = event.getActor();
                float heal = event.getDamage() * attacker.getStats().get(StatType.LIFE_STEAL);
                event.addHealing(heal * event.getOnHitScale());
            }
        });
    }
}

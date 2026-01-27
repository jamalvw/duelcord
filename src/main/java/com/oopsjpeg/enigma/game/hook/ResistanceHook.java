package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.event.DamageEvent;

public class ResistanceHook extends Hook<DamageEvent> {
    public ResistanceHook() {
        super(EventType.DAMAGE_ALL, Priority.RESISTANCE, e -> {
            e.multiplyDamage(1 - e.getVictim().getResist());

            if (e.isSkill())
                e.multiplyDamage(1 - e.getVictim().getStats().get(StatType.SKILL_RESIST));
        });
    }
}

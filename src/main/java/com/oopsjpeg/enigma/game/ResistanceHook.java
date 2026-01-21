package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamagePhase;

public class ResistanceHook implements Hook<DamageEvent> {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.RESISTANCE;
    }

    @Override
    public void execute(DamageEvent event) {
        event.multiplyDamage(1 - event.getVictim().getResist());

        if (event.isSkill())
            event.multiplyDamage(1 - event.getVictim().getStats().get(StatType.SKILL_RESIST));
    }
}

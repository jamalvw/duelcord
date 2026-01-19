package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;

public class ResistanceHook implements DamageHook {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.RESISTANCE;
    }

    @Override
    public void execute(DamageEvent event) {
        event.multiplyDamage(1 - event.getVictim().getResist());
    }
}

package com.oopsjpeg.enigma;

import com.oopsjpeg.enigma.game.DamageEvent;

public interface DamageHook {
    public DamagePhase getPhase();

    public void execute(DamageEvent event);
}

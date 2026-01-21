package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamagePhase;

public interface Hook<T extends Event> {
    DamagePhase getPhase();

    void execute(T event);
}

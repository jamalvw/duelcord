package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.event.DamageEvent;

import java.util.Arrays;

public enum GameMode {
    DUEL("Duel", 2, true, false);

    private final String name;
    private final int size;
    private final boolean ranked;
    private final boolean distortionsEnabled;

    GameMode(String name, int size, boolean ranked, boolean distortionsEnabled) {
        this.name = name;
        this.size = size;
        this.ranked = ranked;
        this.distortionsEnabled = distortionsEnabled;
    }

    public static GameMode fromName(String name) {
        return Arrays.stream(values())
                .filter(g -> name.equalsIgnoreCase(g.getName()) || (name.length() >= 3
                        && g.getName().toLowerCase().startsWith(name.toLowerCase())))
                .findAny().orElse(null);
    }

    public int handleGold(int gold) {
        return gold;
    }

    public DamageEvent handleDamage(DamageEvent event) {
        return event;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isRanked() {
        return this.ranked;
    }

    public boolean isDistortionsEnabled() {
        return distortionsEnabled;
    }
}

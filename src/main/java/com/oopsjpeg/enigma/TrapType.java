package com.oopsjpeg.enigma;

public enum TrapType {
    ATTACK("Attack"),
    DEFEND("Defend"),
    SKILL("Skill");

    private final String name;

    TrapType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

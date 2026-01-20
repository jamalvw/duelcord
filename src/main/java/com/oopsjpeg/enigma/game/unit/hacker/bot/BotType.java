package com.oopsjpeg.enigma.game.unit.hacker.bot;

public enum BotType {
    ATTACK("Attack"),
    SKILL("Skill"),
    DEFEND("Defend");

    private final String name;

    BotType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

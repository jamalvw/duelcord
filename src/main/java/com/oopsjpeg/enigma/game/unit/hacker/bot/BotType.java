package com.oopsjpeg.enigma.game.unit.hacker.bot;

public enum BotType {
    ATTACK("Attack Bot"),
    SKILL("Skill Bot"),
    DEFEND("Defend Bot");

    private final String name;

    BotType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

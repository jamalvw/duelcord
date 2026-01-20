package com.oopsjpeg.enigma.game.unit.hacker.bot;

public class Bot {
    private final BotType type;

    public Bot(BotType type) {
        this.type = type;
    }

    public BotType getType() {
        return type;
    }
}

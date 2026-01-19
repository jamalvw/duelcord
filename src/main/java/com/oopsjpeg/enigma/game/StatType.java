package com.oopsjpeg.enigma.game;

public enum StatType {
    MAX_HEALTH("Max Health"),
    MAX_ENERGY("Max Energy"),
    ATTACK_POWER("Attack Power"),
    ATTACK_COST("Attack Cost"),
    SKILL_POWER("Skill Power"),
    CRIT_CHANCE("Critical Chance"),
    CRIT_DAMAGE("Critical Damage"),
    LIFE_STEAL("Life Steal"),
    RESIST("Resist"),
    SKILL_RESIST("Skill Resist"),
    BLOCK_CHANCE("Block Chance"),
    DODGE("Dodge"),
    COOLDOWN_REDUCTION("Cooldown Reduction"),
    GOLD_PER_TURN("Gold per turn"),
    HEALTH_PER_TURN("Health per turn");

    private final String name;

    StatType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

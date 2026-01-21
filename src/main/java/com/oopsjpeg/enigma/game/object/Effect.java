package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;
import com.oopsjpeg.enigma.game.Stats;

public abstract class Effect extends GameObject {
    private final GameMember owner;
    private final String name;
    private final float power;
    private final Stats stats;

    public Effect(GameMember owner, String name, float power, Stats stats)
    {
        this.owner = owner;
        this.name = name;
        this.power = power;
        this.stats = stats;
    }

    public GameMember getOwner() {
        return owner;
    }

    public String getName()
    {
        return name;
    }

    public float getPower()
    {
        return power;
    }

    public abstract String getDescription();

    public Stats getStats()
    {
        return stats != null ? stats : new Stats();
    }

    @Override
    public String toString()
    {
        return getName();
    }
}

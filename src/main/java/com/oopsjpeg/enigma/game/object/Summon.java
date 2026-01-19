package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;

import static java.lang.Math.round;

public abstract class Summon implements GameObject {
    private final String name;
    private final GameMember owner;
    private float health;
    private boolean shouldRemove = false;
    private boolean isBlocker = false;

    public Summon(String name, GameMember owner, float health, boolean isBlocker)
    {
        this.name = name;
        this.owner = owner;
        this.health = health;
        this.isBlocker = isBlocker;
    }

    public String getName() {
        return name;
    }

    public GameMember getOwner() {
        return owner;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void takeHealth(float health)
    {
        setHealth(getHealth() - health);
    }

    public boolean isBlocker() {
        return isBlocker;
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public void remove()
    {
        shouldRemove = true;
    }

    @Override
    public String getStatus(GameMember member) {
        return getName() + " (" + round(health) + " HP)";
    }
}

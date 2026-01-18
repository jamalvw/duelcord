package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;

import static java.lang.Math.round;

public class Summon implements GameObject {
    private final String name;
    private float health;
    private boolean shouldRemove = false;
    private boolean isBlocker = false;

    public Summon(String name, float health, boolean isBlocker)
    {
        this.name = name;
        this.health = health;
        this.isBlocker = isBlocker;
    }

    public String getName() {
        return name;
    }

    public float getHealth() {
        return health;
    }

    public float takeHealth(float f)
    {
        health -= f;
        return health;
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

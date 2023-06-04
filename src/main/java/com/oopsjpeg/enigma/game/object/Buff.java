package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameObject;
import com.oopsjpeg.enigma.game.Stats;

public abstract class Buff implements GameObject {
    private final String name;
    private final boolean debuff;
    private final GameMember source;
    private final float power;

    private int totalTurns;
    private int currentTurns;
    private boolean shouldRemove = false;

    public Buff(String name, boolean debuff, GameMember source, int totalTurns, float power) {
        this.name = name;
        this.debuff = debuff;
        this.source = source;
        this.totalTurns = totalTurns;
        this.power = power;

        currentTurns = totalTurns;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getTopic(GameMember member) {
        return new String[] {
                getName() + ": " + (totalTurns - currentTurns) + " turns"
        };
    }

    public boolean turn() {
        currentTurns--;
        return shouldRemove();
    }

    public boolean isDebuff() {
        return debuff;
    }

    public GameMember getSource() {
        return source;
    }

    public float getPower() {
        return power;
    }

    public boolean hasPower() {
        return power != 0;
    }

    public String formatPower() {
        return String.valueOf((int) Math.ceil(getPower()));
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public int getCurrentTurns() {
        return currentTurns;
    }

    public void setCurrentTurns(int currentTurns) {
        this.currentTurns = currentTurns;
    }

    public boolean shouldRemove() {
        return shouldRemove || currentTurns == 0;
    }

    public void remove() {
        shouldRemove = true;
    }

    public Stats getStats() {
        return new Stats();
    }

    @Override
    public String toString() {
        return getName();
    }
}

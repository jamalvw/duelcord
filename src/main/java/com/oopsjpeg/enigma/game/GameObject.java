package com.oopsjpeg.enigma.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameObject {
    private final Map<Class<? extends Event>, List<Hook<?>>> hooks = new HashMap<>();

    protected <T extends Event> void hook(Class<T> eventClass, Hook<T> hook) {
        hooks.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(hook);
    }

    public List<Hook<?>> getHooks(Class<? extends Event> eventClass) {
        return new ArrayList<>(hooks.getOrDefault(eventClass, new ArrayList<>()));
    }

    public String getStatus(GameMember member) {
        return null;
    }

    public String onTurnStart(GameMember member) {
        return null;
    }

    public String onTurnEnd(GameMember member) {
        return null;
    }

    public String onDefend(GameMember member) {
        return null;
    }

    public String onSkillUsed(GameMember member) {
        return null;
    }

    public float onHeal(float healAmount) {
        return healAmount;
    }

    public float onShield(float shieldAmount) {
        return shieldAmount;
    }

    public String onShieldBreak(GameMember member) {
        return null;
    }
}

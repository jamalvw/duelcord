package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.event.HealEvent;
import com.oopsjpeg.enigma.game.event.ShieldEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GameObject {
    private final Map<EventType, List<Hook<? extends Event>>> hooks = new HashMap<>();

    private void hook(EventType type, Priority priority, Consumer<? extends Event> action) {
        hooks.computeIfAbsent(type, k -> new ArrayList<>()).add(new Hook<>(type, priority, action));
    }

    protected void onDamageDealt(Priority priority, Consumer<? extends DamageEvent> action) {
        hook(EventType.DAMAGE_DEALT, priority, action);
    }

    protected void onDamageReceived(Priority priority, Consumer<? extends DamageEvent> action) {
        hook(EventType.DAMAGE_RECEIVED, priority, action);
    }

    protected void onHealReceived(Priority priority, Consumer<? extends HealEvent> action) {
        hook(EventType.HEAL_RECEIVED, priority, action);
    }

    protected void onHealSeen(Priority priority, Consumer<? extends HealEvent> action) {
        hook(EventType.HEAL_SEEN, priority, action);
    }

    protected void onShieldSeen(Priority priority, Consumer<? extends ShieldEvent> action) {
        hook(EventType.SHIELD_SEEN, priority, action);
    }

    public List<Hook<? extends Event>> getHooks(EventType type) {
        return hooks.getOrDefault(type, new ArrayList<>());
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

    public String onShieldBreak(GameMember member) {
        return null;
    }
}

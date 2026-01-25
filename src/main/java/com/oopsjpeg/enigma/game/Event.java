package com.oopsjpeg.enigma.game;

import java.util.ArrayList;
import java.util.List;

public abstract class Event {
    private final GameMember actor;
    private final List<String> output = new ArrayList<>();
    private final List<PendingAction> effects = new ArrayList<>();

    private boolean cancelled = false;

    private String source = "";
    private String emote = "";

    public Event(GameMember actor) {
        this.actor = actor;
    }

    public GameMember getActor() {
        return actor;
    }

    public List<String> getOutput() {
        return output;
    }

    public List<Hook<?>> createPipeline() {
        return new ArrayList<>();
    }

    public List<PendingAction> getEffects() {
        return effects;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public String getSource() {
        return source;
    }

    public boolean hasSource() {
        return source != null && !source.isEmpty();
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEmote() {
        return emote;
    }

    public void setEmote(String emote) {
        this.emote = emote;
    }

    public void proposeEffect(PendingAction action) {
        this.effects.add(action);
    }

    public Game getGame() {
        return actor.getGame();
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void dispatchTo(Hook<T> hook) {
        hook.execute((T) this);
    }

    public abstract void complete();
}

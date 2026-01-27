package com.oopsjpeg.enigma.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Event {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final GameMember actor;
    private final List<String> output = new ArrayList<>();
    private final List<Runnable> successTasks = new ArrayList<>();

    private String source;
    private String emote;

    private boolean cancelled = false;

    public Event(GameMember actor, String emote) {
        this.actor = actor;
        this.emote = emote;
    }

    public GameMember getActor() {
        return actor;
    }

    public List<String> getOutput() {
        return output;
    }

    public List<Runnable> getSuccessTasks() {
        return successTasks;
    }

    /**
     * Queues an action to run only if this event is not canceled
     */
    public void queueAction(Runnable task) {
        successTasks.add(task);
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

    public boolean hasEmote() {
        return emote != null && !emote.isEmpty();
    }

    public void setEmote(String emote) {
        this.emote = emote;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public Game getGame() {
        return actor.getGame();
    }

    public abstract List<Hook<? extends Event>> createPipeline();

    public abstract void complete();

    public void finish() {
        complete();

        getSuccessTasks().forEach(task -> {
            LOGGER.debug("Executing success task {}", task.getClass().getSimpleName());
            task.run();
        });
    }
}

package com.oopsjpeg.enigma.game;

import java.util.function.Consumer;

public class Hook<T extends Event> {
    private final Priority priority;
    private final EventType eventType;
    private final Consumer<T> action;

    public Hook(EventType eventType, Priority priority, Consumer<T> action) {
        this.eventType = eventType;
        this.priority = priority;
        this.action = action;
    }

    public Priority getPriority() {
        return priority;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void execute(T event) {
        action.accept(event);
    }
}

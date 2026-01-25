package com.oopsjpeg.enigma;

import discord4j.core.event.domain.interaction.ComponentInteractionEvent;

import java.time.Instant;
import java.util.function.Consumer;

public class EnigmaComponent {
    private final String id;
    private final Consumer<ComponentInteractionEvent> callback;
    private final Instant registeredAt;

    public EnigmaComponent(String id, Consumer<ComponentInteractionEvent> callback) {
        this.id = id;
        this.callback = callback;
        this.registeredAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Consumer<ComponentInteractionEvent> getCallback() {
        return callback;
    }

    public void execute(ComponentInteractionEvent event) {
        callback.accept(event);
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}

package com.oopsjpeg.enigma;

import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// TODO expire old components after a certain period
public class ComponentManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Map<String, EnigmaComponent> components = new ConcurrentHashMap<>();

    public EnigmaComponent register(Consumer<ComponentInteractionEvent> callback) {
        String id = UUID.randomUUID().toString();
        LOGGER.debug("Registering new component with ID: {}", id);
        EnigmaComponent component = new EnigmaComponent(id, callback);
        components.put(id, component);
        return component;
    }

    public void execute(ComponentInteractionEvent event) {
        EnigmaComponent component = components.get(event.getCustomId());
        if (component != null) {
            LOGGER.debug("Executing component with ID: {}", event.getCustomId());
            component.getCallback().accept(event);
        }
    }
}

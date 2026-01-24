package com.oopsjpeg.enigma.service;

import com.oopsjpeg.enigma.storage.Player;
import discord4j.core.object.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerService {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    public Player getOrCreate(User user) {
        if (user == null || user.isBot()) return null;

        return players.computeIfAbsent(user.getId().asString(), Player::new);
    }

    public Player get(User user) {
        if (user == null) return null;

        return get(user.getId().asString());
    }

    public Player get(String id) {
        if (id == null) return null;

        return players.get(id);
    }
}

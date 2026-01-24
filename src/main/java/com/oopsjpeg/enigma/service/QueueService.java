package com.oopsjpeg.enigma.service;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameMode;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QueueService {
    private final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private final Map<GameMode, Set<Player>> queues = new ConcurrentHashMap<>();
    private final Map<String, GameMode> registry = new ConcurrentHashMap<>();

    public void addToQueue(Player player, GameMode mode) {
        if (player == null || mode == null) return;

        queues.computeIfAbsent(mode, k -> new HashSet<>()).add(player);
        registry.put(player.getId(), mode);

        LOGGER.debug("Player {} added to queue for {}", player.getUser().getUsername(), mode.getName());
    }

    public void removeFromQueue(Player player) {
        if (player == null) return;
        GameMode mode = registry.remove(player.getId());
        if (mode == null) return;
        queues.get(mode).remove(player);

        LOGGER.debug("Player {} removed from queue for {}", player.getUser().getUsername(), mode.getName());
    }

    public boolean isInQueue(Player player) {
        return registry.containsKey(player.getId());
    }

    public void refresh() {
        queues.forEach((mode, playersInQueue) -> {
            List<Player> matchedPlayers = new ArrayList<>();

            Iterator<Player> it = playersInQueue.iterator();
            while (it.hasNext()) {
                Player player = it.next();
                matchedPlayers.add(player);

                LOGGER.debug("Checking if {} can join {} ({} players in queue)", player.getUser().getUsername(), mode.getName(), matchedPlayers.size());

                if (matchedPlayers.size() == mode.getSize()) {
                    Game game = new Game(Enigma.getInstance(), mode, matchedPlayers);

                    Enigma.getInstance().getGames().add(game);

                    matchedPlayers.forEach(p ->
                    {
                        p.setGame(game);
                        playersInQueue.remove(p);
                        registry.remove(p.getId());
                    });

                    Util.send(Enigma.getInstance().getMatchmakingChannel(), "**" + mode.getName() + "** has been found for "
                                    + game.getUsers().stream().map(User::getUsername).collect(Collectors.joining(", ")),
                            "Go to " + game.getChannel().getMention() + " to play the match!");

                    LOGGER.debug("Match found for {} players in queue for {}", matchedPlayers.size(), mode.getName());

                    break;
                }
            }
        });
    }
}

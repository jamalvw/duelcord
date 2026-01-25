package com.oopsjpeg.enigma.service;

import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameMode;
import com.oopsjpeg.enigma.game.GameState;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static com.oopsjpeg.enigma.Enigma.SCHEDULER;

public class GameService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final List<Game> games = new CopyOnWriteArrayList<>();
    private final Map<String, Game> playerToGameRegistry = new ConcurrentHashMap<>();

    public Game findGame(Player player) {
        if (player == null) return null;

        return playerToGameRegistry.get(player.getId());
    }

    public GameMember findMember(Player player) {
        Game game = findGame(player);
        if (game == null) return null;
        return game.getMember(player);
    }

    public Game createGame(GameMode mode, Collection<Player> players) {
        LOGGER.debug("Creating game with mode {} and {} players", mode, players.size());

        Game game = new Game(Enigma.getInstance(), mode, players);
        players.forEach(player -> playerToGameRegistry.put(player.getId(), game));
        games.add(game);

        // TODO: game IDs
        LOGGER.debug("Game created with ID {}", "none");
        return game;
    }

    public void destroyGame(Game game) {
        LOGGER.debug("Destroying game with ID {}", "none");
        if (game.getTurnCount() > 7 && game.getMode().isRanked()) {
            GameMember winner = game.getWinner();
            List<GameMember> losers = game.getDead();

            // Winner
            winner.getPlayer().win(losers.get(0).getRankedPoints());
            winner.getPlayer().addGems(Util.nextInt(25, 40));
            //winner.getPlayer().getUnitData(winner.getUnit().getName()).addPoints(Util.nextInt(160, 200));
            //mongo.savePlayer(winner.getPlayer());

            Enigma.getInstance().removeListener(game.getCommandListener());
            //game.getMembers().stream().flatMap(m -> getListeners().stream()).forEach(this::removeListener);

            // Losers
            for (GameMember loser : losers) {
                loser.getPlayer().lose(winner.getRankedPoints());
                loser.getPlayer().addGems(Util.nextInt(10, 20));
                //loser.getPlayer().getUnitData(loser.getUnit().getName()).addPoints(Util.nextInt(80, 100));
                //mongo.savePlayer(loser.getPlayer());
            }

            // Send log embed
            EmbedCreateSpec.Builder logEmbed = EmbedCreateSpec.builder();
            LocalDateTime now = LocalDateTime.now();
            logEmbed.color(Color.YELLOW);
            logEmbed.author("Victory by " + winner.getUsername() + " on " + game.getMode().getName(), null, winner.getUser().getAvatarUrl());
            //logEmbed.description("Playing as **" + winner.getUnit().getName() + "** (" + winner.getUnitData().getPoints() + " pts)"
            //        + "\n**" + winner.getPlayer().getWins() + "** wins and **" + winner.getPlayer().getLosses() + "** losses."
            //        + "\n**" + game.getTurnCount() + "** turns and **" + game.getActions().size() + "** actions."
            //        + "\nOpponent(s): " + game.getDead().stream()
            //        .map(loser -> loser.getUsername() + " (" + loser.getUnit().getName() + ")")
            //        .collect(Collectors.joining(", ")));
            logEmbed.footer(now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth(), null);

            Enigma.getInstance().getMatchLogChannel().createMessage(logEmbed.build()).subscribe();
        }

        Enigma.getInstance().removeListener(game.getCommandListener());

        SCHEDULER.schedule(() -> game.getChannel().delete().subscribe(), 2, TimeUnit.MINUTES);

        playerToGameRegistry.values().remove(game);
        games.remove(game);

        LOGGER.debug("Game destroyed with ID {}", "none");
    }

    public void checkForAfkGames() {
        LOGGER.debug("Checking for AFK games");
        games.stream()
                .filter(g -> g.getGameState() == GameState.PLAYING)
                .forEach(g ->
                {
                    g.getAfkTimer().stack();
                    if (g.getAfkTimer().getCurrent() == g.getAfkTimer().getMax() / 2)
                        g.getChannel().createMessage(Emote.WARN + g.getCurrentMember().getMention() + ", you have **" + (g.getAfkTimer().getMax() / 2) + "** minutes to perform an action, otherwise you will **forfeit**.").subscribe();
                    else if (g.getAfkTimer().isDone())
                        g.getChannel().createMessage(g.getCurrentMember().lose()).subscribe();
                });
    }
}

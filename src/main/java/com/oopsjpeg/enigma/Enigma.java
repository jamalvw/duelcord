package com.oopsjpeg.enigma;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.listener.CommandListener;
import com.oopsjpeg.enigma.listener.ComponentListener;
import com.oopsjpeg.enigma.listener.ReadyListener;
import com.oopsjpeg.enigma.service.PlayerService;
import com.oopsjpeg.enigma.service.QueueService;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Config;
import com.oopsjpeg.enigma.util.ConfigException;
import com.oopsjpeg.enigma.util.Listener;
import com.oopsjpeg.enigma.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Enigma
{
    public static final Logger LOGGER = LoggerFactory.getLogger(Enigma.class);
    public static final Gson GSON = new GsonBuilder().create();
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    private static Enigma instance;

    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final LinkedList<Game> games = new LinkedList<>();
    private final PlayerService playerService = new PlayerService();
    private final QueueService queueService = new QueueService();
    //private MongoManager mongo;
    private GatewayDiscordClient client;
    private CommandListener commands;

    public static File getSettingsFile()
    {
        return new File("enigma.properties");
    }

    public static void main(String[] args) throws ConfigException, IOException
    {
        instance = new Enigma();
        instance.start();
    }

    public static Enigma getInstance()
    {
        return Enigma.instance;
    }

    private void start() throws ConfigException, IOException
    {
        LOGGER.info("Loading configuration..");
        loadConfig();

        // Create mongo manager
        //mongo = new MongoManager(settings.get(Settings.MONGO_HOST), settings.get(Settings.MONGO_DATABASE));

        // Create Discord client
        DiscordClient client = DiscordClientBuilder.create(Config.getBotToken()).build();

        // Create command listener
        commands = new CommandListener(this, Config.getPrimaryPrefix(), GeneralCommand.values());

        // Log in client
        this.client = client.login().block();

        // Add listeners
        addListener(new ReadyListener(this));
        addListener(new ComponentListener(this));
        addListener(commands);
    }

    public void loadConfig() throws IOException, ConfigException
    {
        File configFile = new File(Config.CONFIG_FILE);

        if (!configFile.exists())
        {
            Config.store();
            throw new ConfigException("Configuration file created");
        }

        Config.load();

        if (Config.getBotToken().isEmpty())
            throw new ConfigException("Bot token can't be empty");
        if (Config.getPrimaryPrefix().isEmpty())
            throw new ConfigException("Primary prefix can't be empty");
        if (Config.getGamePrefix().isEmpty())
            throw new ConfigException("Game prefix can't be empty");
    }

    public void addListener(Listener listener)
    {
        listener.register(client);
        listeners.add(listener);
        LOGGER.info("Added listener of class '" + listener.getClass().getName() + "'.");
    }

    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
        LOGGER.info("Removed listener of class '" + listener.getClass().getName() + "'.");
    }

    public static GameMember getGameMemberFromMessage(Message message)
    {
        User user = message.getAuthor().get();
        Player player = Enigma.getInstance().playerService.get(user);
        if (!player.isInGame()) return null;
        return player.getGame().getMember(user);
    }

    public QueueService getQueueService() {
        return queueService;
    }

    public void endGame(Game game)
    {
        if (game.getTurnCount() > 7 && game.getMode().isRanked())
        {
            GameMember winner = game.getWinner();
            List<GameMember> losers = game.getDead();

            // Winner
            winner.getPlayer().win(losers.get(0).getRankedPoints());
            winner.getPlayer().addGems(Util.nextInt(25, 40));
            //winner.getPlayer().getUnitData(winner.getUnit().getName()).addPoints(Util.nextInt(160, 200));
            //mongo.savePlayer(winner.getPlayer());

            removeListener(game.getCommandListener());
            //game.getMembers().stream().flatMap(m -> getListeners().stream()).forEach(this::removeListener);

            // Losers
            for (GameMember loser : losers)
            {
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

            getMatchLogChannel().createMessage(logEmbed.build()).subscribe();
        }

        game.getPlayers().forEach(Player::removeGame);
        listeners.remove(game.getCommandListener());
        games.remove(game);

        SCHEDULER.schedule(() -> game.getChannel().delete().subscribe(), 2, TimeUnit.MINUTES);
    }

    public Guild getGuild()
    {
        return client.getGuildById(Snowflake.of(Config.getGuildId())).block();
    }

    public TextChannel getUnitsChannel()
    {
        return client.getChannelById(Snowflake.of(Config.getUnitsChannelId())).cast(TextChannel.class).block();
    }

    public TextChannel getMatchmakingChannel()
    {
        return client.getChannelById(Snowflake.of(Config.getMatchmakingChannelId())).cast(TextChannel.class).block();
    }

    public TextChannel getMatchLogChannel()
    {
        return client.getChannelById(Snowflake.of(Config.getMatchLogChannelId())).cast(TextChannel.class).block();
    }

    public TextChannel getLeaderboardChannel()
    {
        return client.getChannelById(Snowflake.of(Config.getLeaderboardChannelId())).cast(TextChannel.class).block();
    }

    //public MongoManager getMongo() {
    //    return this.mongo;
    //}

    public GatewayDiscordClient getClient()
    {
        return this.client;
    }

    public ArrayList<Listener> getListeners()
    {
        return this.listeners;
    }

    public CommandListener getCommands()
    {
        return this.commands;
    }

    public LinkedList<Game> getGames()
    {
        return this.games;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }
}

package com.oopsjpeg.enigma;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oopsjpeg.enigma.listener.CommandListener;
import com.oopsjpeg.enigma.listener.ComponentListener;
import com.oopsjpeg.enigma.listener.ReadyListener;
import com.oopsjpeg.enigma.service.GameService;
import com.oopsjpeg.enigma.service.PlayerService;
import com.oopsjpeg.enigma.service.QueueService;
import com.oopsjpeg.enigma.util.Config;
import com.oopsjpeg.enigma.util.ConfigException;
import com.oopsjpeg.enigma.util.Listener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Enigma
{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final Gson GSON = new GsonBuilder().create();
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    private static Enigma instance;

    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final GameService gameService = new GameService();
    private final PlayerService playerService = new PlayerService();
    private final QueueService queueService = new QueueService();
    private final ComponentManager componentManager = new ComponentManager();
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

        this.client.on(ComponentInteractionEvent.class).subscribe(componentManager::execute);
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

    public QueueService getQueueService() {
        return queueService;
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

    public GameService getGameService() {
        return gameService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }
}

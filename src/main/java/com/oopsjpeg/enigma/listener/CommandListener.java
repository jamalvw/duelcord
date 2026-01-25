package com.oopsjpeg.enigma.listener;

import com.oopsjpeg.enigma.Command;
import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.util.Listener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class CommandListener implements Listener {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Enigma instance;
    private final String prefix;
    private final LinkedList<Command> commands;
    private MessageChannel limit;
    private User userLimit;

    public CommandListener(Enigma instance, String prefix, Command[] commands) {
        this.instance = instance;
        this.prefix = prefix;
        this.commands = new LinkedList<>(Arrays.asList(commands));
    }

    public CommandListener(Enigma instance, String prefix, Command[] commands, MessageChannel limit) {
        this(instance, prefix, commands);
        this.limit = limit;
    }

    public CommandListener(Enigma instance, String prefix, LinkedList<Command> commands) {
        this.instance = instance;
        this.prefix = prefix;
        this.commands = commands;
    }

    @Override
    public void register(GatewayDiscordClient client) {
        client.on(MessageCreateEvent.class, this::onMessageCreate).subscribe();
    }

    private Mono<?> onMessageCreate(MessageCreateEvent event) {
        GatewayDiscordClient client = event.getClient();
        Message message = event.getMessage();
        User author = message.getAuthor().orElse(null);
        String content = message.getContent();
        MessageChannel channel = message.getChannel().block();

        if (author == null || channel == null
                || (limit != null && !channel.equals(limit))
                || (userLimit != null && !author.equals(userLimit))
                || author.getId().equals(client.getSelfId())
                || !content.toLowerCase().startsWith(prefix.toLowerCase()))
            return Mono.empty();

        String[] split = content.split(" ");
        String alias = split[0].replaceFirst(prefix, "");
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        Command command = Command.get(commands, author, alias);

        if (command == null) return Mono.empty();

        LOGGER.debug("{} issued command {} with args {}", author.getUsername(), alias, Arrays.toString(args));

        return command.execute(message, args);
    }

    public Enigma getInstance() {
        return this.instance;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public LinkedList<Command> getCommands() {
        return this.commands;
    }

    public MessageChannel getLimit() {
        return this.limit;
    }

    public void setLimit(TextChannel limit) {
        this.limit = limit;
    }

    public User getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(User userLimit) {
        this.userLimit = userLimit;
    }
}

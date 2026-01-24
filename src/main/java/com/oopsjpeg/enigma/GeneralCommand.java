package com.oopsjpeg.enigma;

import com.oopsjpeg.enigma.game.Build;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.GameMode;
import com.oopsjpeg.enigma.game.Tree;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.unit.Units;
import com.oopsjpeg.enigma.service.PlayerService;
import com.oopsjpeg.enigma.service.QueueService;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum GeneralCommand implements Command
{
    BUILD("build")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    MessageChannel channel = message.getChannel().block();
                    if (args[0].equalsIgnoreCase("items"))
                    {
                        channel.createMessage(buildItemTree(Tree.BASIC)).subscribe();
                        channel.createMessage(buildItemTree(Tree.ADVANCED)).subscribe();
                        channel.createMessage(buildItemTree(Tree.COMPLETE)).subscribe();
                    } else if (args[0].equalsIgnoreCase("units"))
                    {
                        // Create a list of select menu options for units
                        List<SelectMenu.Option> options = Arrays.stream(Units.values())
                                .map(unit -> SelectMenu.Option.of(unit.getName(), unit.name()))
                                .collect(Collectors.toList());

                        channel.createMessage(MessageCreateSpec.builder()
                                .content("Select a unit using the dropdown menu below to review it.")
                                .addComponent(
                                        ActionRow.of(
                                                SelectMenu.of("unit_viewer", options)
                                        ))
                                .build()).subscribe();
                    }
                }

                @Override
                public PermissionSet getPermissions()
                {
                    return PermissionSet.of(Permission.MANAGE_GUILD);
                }

                public EmbedCreateSpec buildItemTree(Tree tree)
                {
                    EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
                    embed.title("**" + tree.getName() + "**");
                    embed.color(tree.getColor());

                    Arrays.stream(Items.values())
                            .filter(item -> item.getTree() == tree)
                            .map(item -> item.create(null))
                            .filter(Item::isBuyable)
                            .sorted(Comparator.comparingInt(Item::getCost))
                            .forEach(i ->
                            {
                                String value = (i.hasBuild() ? "[" + Arrays.stream(i.getBuild())
                                        .map(c -> "`" + c.getName() + "`")
                                        .collect(Collectors.joining(", ")) + "]" : "") + "\n" +
                                        Util.formatStats(i.getStats()) + "\n" +
                                        (i.hasTip() ? "*" + i.getTip() + "*\n" : "");

                                embed.addField(i.getName() + " (__" + i.getCost() + "g__)", value, true);
                            });

                    return embed.build();
                }
            },
    QUEUE("q")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    MessageChannel channel = message.getChannel().block();
                    User author = message.getAuthor().orElse(null);
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    Player player = playerService.getOrCreate(author);
                    QueueService queueService = Enigma.getInstance().getQueueService();

                    if (player.getGame() != null)
                        Util.sendFailure(channel, "You're already in a match.");
                    else if (!channel.equals(Enigma.getInstance().getMatchmakingChannel()))
                        Util.sendFailure(channel, "You must be in " + Enigma.getInstance().getMatchmakingChannel().getMention() + " to queue for games.");
                    else if (queueService.isInQueue(player)) {
                        queueService.removeFromQueue(player);
                        Util.sendFailure(channel, "You have left the queue.");
                    } else {
                        GameMode mode = args.length > 0 ? GameMode.fromName(args[0]) : GameMode.DUEL;
                        if (mode == null)
                            Util.sendFailure(channel, "Invalid game mode.");
                        else
                        {
                            if (player.isSpectating())
                                player.removeSpectate();
                            queueService.addToQueue(player, mode);
                            Util.sendSuccess(channel, "**" + author.getUsername() + "** is in queue for **" + mode.getName() + "**.");
                        }
                    }
                }
            },
    STATS("stats")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    MessageChannel channel = message.getChannel().block();
                    User author = message.getAuthor().orElse(null);
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    Player player = playerService.get(author);

                    if (player == null) {
                        Util.sendFailure(channel, "You haven't played any matches yet.");
                        return;
                    }

                    channel.createEmbed(e ->
                    {
                        e.setAuthor(author.getUsername() + " (" + Math.round(player.getRankedPoints()) + " RP)", null, author.getAvatarUrl());
                        e.setDescription("**" + player.getWins() + "**W **" + player.getLosses() + "**L (**" + Util.percent(player.getWinRate()) + "** WR)"
                                + "\nGems: **" + player.getGems() + "**");
                        //if (!player.getUnitDatas().isEmpty())
                        //    e.addField("Top Units", player.getUnitDatas().stream()
                        //                        .sorted(Comparator.comparingInt(Player.UnitData::getPoints).reversed())
                        //                .limit(3)
                        //                .map(data -> data.getUnitName() + " (" + data.getPoints() + " pts)")
                        //                .collect(Collectors.joining("\n")), true);
                    }).subscribe();
                }
            },
    LEADERBOARD("leaderboard")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    MessageChannel channel = message.getChannel().block();
                    channel.createEmbed(Util.leaderboard()).subscribe();
                }
            },
    SPECTATE("spectate")
            {
                @Override
                public void execute(Message message, String[] args) {
                    MessageChannel channel = message.getChannel().block();
                    User author = message.getAuthor().orElse(null);
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    Player player = playerService.getOrCreate(author);

                    if (player.isSpectating()) {
                        player.removeSpectate();
                        Util.sendFailure(channel, "You have stopped spectating.");
                        return;
                    }

                    if (player.isInGame()) {
                        Util.sendFailure(channel, "You can't spectate while in a match.");
                        return;
                    }

                    if (!(channel instanceof TextChannel)) {
                        Util.sendFailure(channel, "You can only spectate in a server.");
                        return;
                    }

                    if (args.length < 1) {
                        Util.sendFailure(channel, "You must specify a player to spectate.");
                        return;
                    }

                    Guild guild = ((TextChannel) channel).getGuild().block();
                    String search = args[0].toLowerCase();

                    // - disclaimer -
                    // inconveniently, this doesn't support mentions,
                    // but that won't matter when slash commands are implemented
                    User target = guild.searchMembers(search, 1)
                            .filter(m -> m.getUsername().toLowerCase().equals(search))
                            .blockFirst();

                    if (target == null) {
                        Util.sendFailure(channel, "That player either doesn't exist or can't be spectated.");
                        return;
                    }

                    if (target.getId().equals(author.getId())) {
                        Util.sendFailure(channel, "You can't spectate yourself. That'd be weird.");
                        return;
                    }

                    Player targetPlayer = playerService.get(target);

                    if (targetPlayer == null || !targetPlayer.isInGame()) {
                        Util.sendFailure(channel, "That player isn't in a match.");
                        return;
                    }

                    player.setSpectateId(target.getId().asString());
                    Util.sendSuccess(channel, "You are now spectating **" + target.getUsername() + "**" + " in " + targetPlayer.getGame().getChannel().getMention() + ".");
                }
            },
    ITEM("item")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    GameMember member = Enigma.getGameMemberFromMessage(message);
                    Items query = Items.fromName(String.join(" ", args));

                    if (query == null) return;

                    Item item = query.create(member);
                    int cost = item.getCost();
                    User author = message.getAuthor().get();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    Player player = playerService.get(author);

                    if (player != null && player.isInGame())
                    {
                        Build build = item.build(member.getItems());
                        cost -= build.getReduction();
                    }

                    MessageChannel channel = message.getChannel().block();
                    Util.send(channel, item.getName() + " (" + cost + "g)", Util.joinNonEmpty("\n",
                            item.hasBuild() ? "*Build: " + Arrays.toString(item.getBuild()) + "*\n" : null,
                            Util.formatStats(item.getStats()),
                            Util.formatEffects(item.getEffects())));
                }
            },
    UNIT("unit")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    Units unit = Units.fromName(String.join(" ", args));

                    if (unit == null) return;

                    MessageChannel channel = message.getChannel().block();
                    channel.createMessage(MessageCreateSpec.builder()
                            .addEmbed(unit.create(null).embed())
                            .build()).subscribe();
                }
            };

    private final String name;

    GeneralCommand(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return "placeholder";
    }
}

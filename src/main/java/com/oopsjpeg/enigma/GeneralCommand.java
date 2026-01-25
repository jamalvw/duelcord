package com.oopsjpeg.enigma;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.unit.Units;
import com.oopsjpeg.enigma.service.GameService;
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
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum GeneralCommand implements Command {
    BUILD("build") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            MessageChannel channel = message.getChannel().block();
            if (args[0].equalsIgnoreCase("items")) {
                return channel.createMessage(MessageCreateSpec.builder()
                        .embeds(
                                buildItemTree(Tree.BASIC),
                                buildItemTree(Tree.ADVANCED),
                                buildItemTree(Tree.COMPLETE))
                        .build());
            } else if (args[0].equalsIgnoreCase("units")) {
                // Create a list of select menu options for units
                List<SelectMenu.Option> options = Arrays.stream(Units.values())
                        .map(unit -> SelectMenu.Option.of(unit.getName(), unit.name()))
                        .collect(Collectors.toList());

                return channel.createMessage(MessageCreateSpec.builder()
                        .content("Select a unit using the dropdown menu below to review it.")
                        .addComponent(
                                ActionRow.of(
                                        SelectMenu.of("unit_viewer", options)
                                ))
                        .build());
            }
            return Mono.empty();
        }

        @Override
        public PermissionSet getPermissions() {
            return PermissionSet.of(Permission.MANAGE_GUILD);
        }

        public EmbedCreateSpec buildItemTree(Tree tree) {
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
    QUEUE("q") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            MessageChannel channel = message.getChannel().block();
            User author = message.getAuthor().orElse(null);
            PlayerService playerService = Enigma.getInstance().getPlayerService();
            Player player = playerService.getOrCreate(author);
            QueueService queueService = Enigma.getInstance().getQueueService();
            GameService gameService = Enigma.getInstance().getGameService();

            if (gameService.findGame(player) != null)
                return Util.sendFailure(channel, "You're already in a match.");
            if (!channel.equals(Enigma.getInstance().getMatchmakingChannel()))
                return Util.sendFailure(channel, "You must be in " + Enigma.getInstance().getMatchmakingChannel().getMention() + " to queue for games.");
            if (queueService.isInQueue(player)) {
                queueService.removeFromQueue(player);
                return Util.sendFailure(channel, "You have left the queue.");
            }

            GameMode mode = args.length > 0 ? GameMode.fromName(args[0]) : GameMode.DUEL;
            if (mode == null)
                return Util.sendFailure(channel, "Invalid game mode.");

            queueService.addToQueue(player, mode);
            return Util.sendSuccess(channel, "**" + author.getUsername() + "** is in queue for **" + mode.getName() + "**.");
        }
    },
    STATS("stats") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            MessageChannel channel = message.getChannel().block();
            User author = message.getAuthor().orElse(null);
            PlayerService playerService = Enigma.getInstance().getPlayerService();
            Player player = playerService.get(author);

            if (player == null)
                return Util.sendFailure(channel, "You haven't played any matches yet.");

            return channel.createEmbed(e ->
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
            });
        }
    },
    LEADERBOARD("leaderboard") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            MessageChannel channel = message.getChannel().block();
            return channel.createEmbed(Util.leaderboard());
        }
    },
    SPECTATE("spectate") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            MessageChannel channel = message.getChannel().block();
            User author = message.getAuthor().orElse(null);
            PlayerService playerService = Enigma.getInstance().getPlayerService();
            GameService gameService = Enigma.getInstance().getGameService();
            Player player = playerService.getOrCreate(author);

            if (gameService.isSpectating(player)) {
                gameService.removeSpectator(player);
                return Util.sendFailure(channel, "You have stopped spectating.");
            }

            if (!(channel instanceof TextChannel))
                return Util.sendFailure(channel, "You can only spectate in a server.");

            if (args.length < 1)
                return Util.sendFailure(channel, "You must specify a player to spectate.");

            Guild guild = ((TextChannel) channel).getGuild().block();
            String search = args[0].toLowerCase();

            // - disclaimer -
            // inconveniently, this doesn't support mentions,
            // but that won't matter when slash commands are implemented
            User target = guild.searchMembers(search, 1)
                    .filter(m -> m.getUsername().toLowerCase().equals(search))
                    .blockFirst();

            if (target == null)
                return Util.sendFailure(channel, "That player either doesn't exist or can't be spectated.");

            if (target.getId().equals(author.getId()))
                return Util.sendFailure(channel, "You can't spectate yourself. That'd be weird.");

            Player targetPlayer = playerService.get(target);
            Game game = gameService.findGame(targetPlayer);

            if (targetPlayer == null || game == null)
                return Util.sendFailure(channel, "That player isn't in a match.");

            gameService.addSpectator(game, player);
            return Util.sendSuccess(channel, "You are now spectating **" + target.getUsername() + "**" + " in " + game.getChannel().getMention() + ".");
        }
    },
    ITEM("item") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            Items query = Items.fromName(String.join(" ", args));

            if (query == null) return Mono.empty();

            GameService gameService = Enigma.getInstance().getGameService();
            PlayerService playerService = Enigma.getInstance().getPlayerService();
            GameMember member = gameService.findMember(playerService.get(message.getAuthor().get()));

            Item item = query.create(member);
            int cost = item.getCost();

            if (member != null) {
                Build build = item.build(member.getItems());
                cost -= build.getReduction();
            }

            MessageChannel channel = message.getChannel().block();
            return channel.createMessage(item.embed());
        }
    },
    UNIT("unit") {
        @Override
        public Mono<?> execute(Message message, String[] args) {
            Units unit = Units.fromName(String.join(" ", args));

            if (unit == null) return Mono.empty();

            MessageChannel channel = message.getChannel().block();
            return channel.createMessage(MessageCreateSpec.builder()
                    .addEmbed(unit.create(null).embed())
                    .build());
        }
    };

    private final String name;

    GeneralCommand(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "placeholder";
    }
}

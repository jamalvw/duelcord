package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.Command;
import com.oopsjpeg.enigma.ComponentManager;
import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.EnigmaComponent;
import com.oopsjpeg.enigma.service.GameService;
import com.oopsjpeg.enigma.service.PlayerService;
import com.oopsjpeg.enigma.game.action.AttackAction;
import com.oopsjpeg.enigma.game.action.BuyAction;
import com.oopsjpeg.enigma.game.action.SellAction;
import com.oopsjpeg.enigma.game.action.UseAction;
import com.oopsjpeg.enigma.game.buff.DisarmDebuff;
import com.oopsjpeg.enigma.game.object.Item;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.Units;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GameCommand implements Command
{
    // TODO Energy costs should be found dynamically somehow - changing energy costs of system commands requires changing values in multiple locations
    ATTACK("attack")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));
                    GameMember member = game.getMember(author);

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember()))
                    {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PICKING)
                            Util.sendFailure(channel, "You cannot attack until the game has started.");
                        else if (member.hasBuff(DisarmDebuff.class))
                            Util.sendFailure(channel, "You are currently disarmed and cannot attack.");
                        else if (member.getEnergy() < 50 + member.getStats().get(StatType.ATTACK_COST))
                            Util.sendFailure(channel, "**`>" + getName() + "`** costs **" + 50 + "** energy. You have **" + member.getEnergy() + "**.");
                        else
                            channel.createMessage(member.act(new AttackAction(game.getRandomTarget(member)))).subscribe();
                    }
                }
            },
    BUY("buy")
            {
                @Override
                public void execute(Message message, String[] args) {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));
                    GameMember member = game.getMember(author);

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember())) {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PICKING) {
                            Util.sendFailure(channel, "You cannot buy items until the game has started.");
                            return;
                        }

                        if (!game.getCurrentMember().equals(member)) {
                            Util.sendFailure(channel, "It's not your turn yet.");
                            return;
                        }

                        Items query = Items.fromName(String.join(" ", args));
                        Unit unit = member.getUnit();

                        if (query == null) {
                            Util.sendFailure(channel, "I can't find an item with that name.\n\nTry one of these items on " + unit.getName() + ": " + unit.getRecommendedBuild().stream().map(Items::getName).collect(Collectors.joining(", ")));
                            return;
                        }

                        Item item = query.create(member);
                        if (!item.isBuyable()) {
                            Util.sendFailure(channel, item.getName() + " can't be bought.");
                            return;
                        }

                        Build build = item.build(member.getItems());
                        if (!member.hasGold(build.getCost())) {
                            // TODO: AI made this, CLEAN it
                            String suggestion = "";
                            List<Item> buildPath = Arrays.stream(item.getBuild()).map(i -> i.create(null)).collect(Collectors.toList());
                            List<Item> memberItems = member.getItems();

                            Item cheapestNext = null;
                            for (Item component : buildPath) {
                                if (!memberItems.stream().anyMatch(i -> i.getClass().equals(component.getClass()))) {
                                    if (cheapestNext == null || component.getCost() < cheapestNext.getCost()) {
                                        cheapestNext = component;
                                    }
                                }
                            }

                            if (cheapestNext != null) {
                                suggestion = "\n\nTry buying **" + cheapestNext.getName() + "** first.";
                            }

                            Util.sendFailure(channel, "You need **" + member.getGoldDifference(build.getCost()) + "** more gold for **" + item.getName() + "**." + suggestion);
                            return;
                        }

                        if (build.getPostData().size() >= 5) {
                            Util.sendFailure(channel, "You do not have enough inventory space for a(n) **" + item.getName() + "**.");
                            return;
                        }

                        channel.createMessage(member.act(new BuyAction(build))).subscribe();
                    }
                }
            },
    END("end")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));
                    GameMember member = game.getMember(author);

                    if (!game.getCurrentMember().equals(member)) {
                        Util.sendFailure(channel, "It's not your turn yet.");
                        return;
                    }

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember()))
                    {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PICKING)
                            Util.sendFailure(channel, "You cannot end your turn until the game has started.");
                        else
                            channel.createMessage(game.nextTurn()).subscribe();
                    }
                }
            },
    FORFEIT("ff")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));

                    if (channel.equals(game.getChannel()))
                    {
                        message.delete().subscribe();
                        channel.createMessage(game.getMember(author).lose()).subscribe();
                    }
                }
            },
    PICK("pick")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author.getId().asString()));
                    GameMember member = game.getMember(author);

                    if (!game.getCurrentMember().equals(member)) {
                        Util.sendFailure(channel, "It's not your turn yet.");
                        return;
                    }

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember()))
                    {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PLAYING)
                            Util.sendFailure(channel, "You cannot pick a unit after the game has started.");
                        else
                        {
                            String name = String.join(" ", args).toLowerCase();
                            Units type = name.equals("random")
                                    ? Util.pickRandom(Units.values())
                                    : Units.fromName(name);
                            if (type == null)
                                Util.sendFailure(channel, "Invalid unit.");
                            else {
                                Unit unit = type.create(member);

                                ComponentManager manager = Enigma.getInstance().getComponentManager();

                                EnigmaComponent unitInfo = manager.register(e -> e.reply(InteractionApplicationCommandCallbackSpec.builder()
                                        .ephemeral(true)
                                        .embeds(unit.embed())
                                        .build()).subscribe());
                                EnigmaComponent unitBuildPath = manager.register(e -> e.reply(InteractionApplicationCommandCallbackSpec.builder()
                                        .ephemeral(true)
                                        .content("Typically, **" + unit.getName() + "** should build at least one of these items:")
                                        .embeds(unit.getRecommendedBuild().stream()
                                                .map(i -> i.create(null))
                                                .map(Item::embed)
                                                .collect(Collectors.toList()))
                                        .build()).subscribe());


                                member.setUnit(unit);
                                channel.createMessage(MessageCreateSpec.builder()
                                        .components(
                                                TextDisplay.of(Emote.YES + "**" + author.getUsername() + "** will play as **" + unit.getName() + "**!"),
                                                ActionRow.of(
                                                        Button.secondary(unitInfo.getId(), "What does " + unit.getName() + " do?"),
                                                        Button.secondary(unitBuildPath.getId(), "What should " + unit.getName() + " buy?")),
                                                TextDisplay.of(game.nextTurn()))
                                        .build()).subscribe();
                            }
                        }
                    }
                }
            },
    REFRESH("refresh")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));

                    if (channel.equals(game.getChannel()))
                    {
                        message.delete().subscribe();
                        game.updateStatus();
                    }
                }
            },
    SELL("sell")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));
                    GameMember member = game.getMember(author);

                    if (!game.getCurrentMember().equals(member)) {
                        Util.sendFailure(channel, "It's not your turn yet.");
                        return;
                    }

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember()))
                    {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PICKING)
                            Util.sendFailure(channel, "You cannot sell items until the game has started.");
                        else
                        {
                            Item item = member.getItem(String.join(" ", args));
                            if (item == null)
                                Util.sendFailure(channel, "Invalid item.");
                            else if (!member.getData().contains(item))
                                Util.sendFailure(channel, "You don't have a(n) **" + item.getName() + "**.");
                            else
                                channel.createMessage(member.act(new SellAction(item))).subscribe();
                        }
                    }
                }
            },
    USE("use")
            {
                @Override
                public void execute(Message message, String[] args)
                {
                    User author = message.getAuthor().orElse(null);
                    MessageChannel channel = message.getChannel().block();
                    PlayerService playerService = Enigma.getInstance().getPlayerService();
                    GameService gameService = Enigma.getInstance().getGameService();
                    Game game = gameService.findGame(playerService.get(author));
                    GameMember member = game.getMember(author);

                    if (!game.getCurrentMember().equals(member)) {
                        Util.sendFailure(channel, "It's not your turn yet.");
                        return;
                    }

                    if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember()))
                    {
                        message.delete().subscribe();
                        if (game.getGameState() == GameState.PICKING)
                            Util.sendFailure(channel, "You cannot use items until the game has started.");
                        else
                        {
                            Item item = member.getItem(String.join(" ", args));
                            if (item == null)
                                Util.sendFailure(channel, "Invalid item.");
                            else if (!member.getData().contains(item))
                                Util.sendFailure(channel, "You don't have a(n) **" + item.getName() + "**.");
                            else if (!item.canUse(member))
                                Util.sendFailure(channel, "**" + item.getName() + "** can't be used.");
                            else if (member.getEnergy() < 25)
                                Util.sendFailure(channel, "**`>" + getName() + "`** costs **" + 25 + "** energy. You have **" + member.getEnergy() + "**.");
                            else
                                channel.createMessage(member.act(new UseAction(item))).subscribe();
                        }
                    }
                }
            };

    private final String name;

    GameCommand(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDescription()
    {
        return "placeholder";
    }
}
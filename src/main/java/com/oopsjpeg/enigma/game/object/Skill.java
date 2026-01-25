package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.Command;
import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.SilencedDebuff;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.service.GameService;
import com.oopsjpeg.enigma.service.PlayerService;
import com.oopsjpeg.enigma.storage.Player;
import com.oopsjpeg.enigma.util.Cooldown;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.COOLDOWN_REDUCTION;

public abstract class Skill implements Command, GameAction {
    private final Unit unit;
    private final Cooldown cooldown;
    private final int cost;

    public Skill(Unit unit, int cost) {
        this(unit, cost, 0);
    }

    public Skill(Unit unit, int cost, int cooldown) {
        this.unit = unit;
        this.cost = cost;
        this.cooldown = cooldown > 0 ? new Cooldown(cooldown) : null;
    }

    public Unit getUnit() {
        return unit;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public boolean hasCooldown() {
        return cooldown != null;
    }

    public abstract String getSimpleDescription();

    @Override
    public int getCost(GameMember actor) {
        return cost;
    }

    public String getStatus(GameMember member) {
        return "**`>" + getName() + "`**: " + (!hasCooldown() ? "Ready" : (cooldown.isDone() ? "Ready" : "in " + cooldown.getCurrent() + " turn" + (cooldown.getCurrent() > 1 ? "s" : "")));
    }

    @Override
    public Mono<?> execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel().block();
        GameService gameService = Enigma.getInstance().getGameService();
        PlayerService playerService = Enigma.getInstance().getPlayerService();
        Player player = playerService.get(message.getAuthor().orElse(null));
        GameMember actor = gameService.findMember(player);
        Game game = actor.getGame();

        if (!channel.equals(game.getChannel()) || !actor.equals(game.getCurrentMember()))
            return Mono.empty();

        message.delete().subscribe();

        if (actor.hasBuff(SilencedDebuff.class))
            return Util.sendFailure(channel, "You can't use skills while silenced.");

        if (hasCooldown() && !cooldown.isDone())
            return Util.sendFailure(channel, "**`>" + getName() + "`** will be ready in **" + cooldown.getCurrent() + "** turns.");

        message.delete().subscribe();

        if (hasCost(actor) && actor.getEnergy() < getCost(actor))
            return Util.sendFailure(channel, "**`>" + getName() + "`** costs **" + cost + "** energy. You have **" + actor.getEnergy() + "**.");

        if (hasCost(actor) && actor.getEnergy() < getCost(actor))
            return Util.sendFailure(channel, "**`>" + getName() + "`** costs **" + cost + "** energy. You have **" + actor.getEnergy() + "**.");

        List<String> output = new ArrayList<>();
        if (hasCooldown())
            cooldown.start(actor.getStats().getInt(COOLDOWN_REDUCTION));
        actor.getData().forEach(o -> output.add(o.onSkillUsed(actor)));
        output.add(actor.act(this));

        return channel.createMessage(Util.joinNonEmpty("\n", output));
    }
}

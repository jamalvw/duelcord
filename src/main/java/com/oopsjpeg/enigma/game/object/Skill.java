package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.Command;
import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.SilencedDebuff;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Cooldown;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import static com.oopsjpeg.enigma.game.StatType.COOLDOWN_REDUCTION;

public abstract class Skill implements Command
{
    private final Unit unit;
    private final Cooldown cooldown;
    private final int cost;

    public Skill(Unit unit, int cost)
    {
        this(unit, cost, 0);
    }

    public Skill(Unit unit, int cost, int cooldown)
    {
        this.unit = unit;
        this.cost = cost;
        this.cooldown = cooldown > 0 ? new Cooldown(cooldown) : null;
    }

    public Unit getUnit()
    {
        return unit;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public boolean hasCooldown()
    {
        return cooldown != null;
    }

    public int getCost() {
        return cost;
    }

    public boolean hasCost() {
        return cost > 0;
    }

    public String getStatus(GameMember member)
    {
        return getName() + ": " + (!hasCooldown() ? "Ready" : (cooldown.isDone() ? "Ready" : "in " + cooldown.getCurrent() + " turn" + (cooldown.getCurrent() > 1 ? "s" : "")));
    }

    public abstract GameAction act(GameMember actor);

    @Override
    public void execute(Message message, String[] args)
    {
        MessageChannel channel = message.getChannel().block();
        GameMember actor = Enigma.getGameMemberFromMessage(message);
        Game game = actor.getGame();

        if (!channel.equals(game.getChannel()) || !actor.equals(game.getCurrentMember()))
            return;

        message.delete().subscribe();

        if (actor.hasBuff(SilencedDebuff.class))
        {
            Util.sendFailure(channel, "You can't use skills while silenced.");
            return;
        }

        if (hasCooldown() && !cooldown.isDone())
        {
            Util.sendFailure(channel, "**`>" + getName() + "`** will be ready in **" + cooldown.getCurrent() + "** turns.");
            return;
        }

        if (hasCost() && actor.getEnergy() < getCost())
        {
            Util.sendFailure(channel, "**`>" + getName() + "`** costs **" + cost + "** energy. You have **" + actor.getEnergy() + "**.");
            return;
        }

        actor.act(act(actor));
        cooldown.start(actor.getStats().getInt(COOLDOWN_REDUCTION));
    }
}

package com.oopsjpeg.enigma.game.unit.hacker.buff;

import com.oopsjpeg.enigma.TrapType;
import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Trap;
import com.oopsjpeg.enigma.game.unit.hacker.HackerUnit;
import com.oopsjpeg.enigma.game.unit.hacker.bot.Bot;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotDamageEvent;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotType;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

public class InjectTrap extends Trap {
    public InjectTrap(GameMember owner, GameMember source, int totalTurns, TrapType type) {
        super(owner, source, "Injected", totalTurns, 0, type);
    }

    @Override
    public String onActivated() {
        if (!(getSource().getUnit() instanceof HackerUnit)) return null;

        HackerUnit hacker = (HackerUnit) getSource().getUnit();
        BotType type;

        switch (getType()) {
            case DEFEND:
                type = BotType.DEFEND;
                break;
            case SKILL:
                type = BotType.SKILL;
                break;
            case ATTACK:
                type = BotType.ATTACK;
                break;
            default:
                return null;
        }

        Bot bot = new Bot(type);
        hacker.getBots().add(bot);

        BotDamageEvent botStrike = new BotDamageEvent(bot, getSource(), getOwner());
        return Util.joinNonEmpty("\n", Emote.BUFF + "**" + getSource().getUsername() + "'s Trap** activated and created **" + bot.getType().getName() + "**!", EventDispatcher.dispatch(botStrike));
    }

    @Override
    public String getStatus(GameMember member) {
        return "Injected: Watch your step..";
    }
}

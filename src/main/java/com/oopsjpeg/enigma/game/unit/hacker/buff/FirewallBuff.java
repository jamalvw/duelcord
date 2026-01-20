package com.oopsjpeg.enigma.game.unit.hacker.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.hacker.HackerUnit;
import com.oopsjpeg.enigma.game.unit.hacker.bot.Bot;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotType;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FirewallBuff extends Buff {
    public FirewallBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Firewall", false, totalTurns, power);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Firewall: Test";
    }

    @Override
    public String onShieldBreak(GameMember member) {
        if (!(member.getUnit() instanceof HackerUnit)) return "";

        HackerUnit hacker = (HackerUnit) member.getUnit();

        List<String> botTypes = new ArrayList<>();
        for (int i = 0; i < getPower(); i++) {
            BotType type = Util.pickRandom(BotType.values());
            Bot bot = new Bot(type);
            hacker.getBots().add(bot);
            botTypes.add("**" + type.getName() + " Bot**");

            if (hacker.getBots().size() >= HackerUnit.BOT_LIMIT) break;
        }

        remove(true);

        return Emote.BUFF + "**" + member.getUsername() + "'s Firewall** created " + Util.joinWithAnd(botTypes) + "!";
    }
}

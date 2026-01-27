package com.oopsjpeg.enigma.game.unit.hacker.bot;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.hacker.HackerUnit.*;

public class BotDamageEvent extends DamageEvent {
    private final Bot bot;

    public BotDamageEvent(Bot bot, GameMember attacker, GameMember victim) {
        super(attacker, victim);
        this.bot = bot;

        Stats stats = attacker.getStats();
        setSource(bot.getType().getName());
        setEmote(Emote.BOT);
        setIsOnHit(true);
        setIsSkill(true);
        setIsDoT(true);
        setOnHitScale(BOT_ON_HIT_RATIO);
        addDamage(BOT_DAMAGE);
        addDamage(stats.get(StatType.ATTACK_POWER) * BOT_DAMAGE_AP_RATIO);
        addDamage(stats.get(StatType.SKILL_POWER) * BOT_DAMAGE_SP_RATIO);
    }

    public Bot getBot() {
        return bot;
    }
}

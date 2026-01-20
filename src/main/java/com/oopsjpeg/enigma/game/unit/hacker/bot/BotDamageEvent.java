package com.oopsjpeg.enigma.game.unit.hacker.bot;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.hacker.HackerUnit.*;

public class BotDamageEvent extends DamageEvent {
    public BotDamageEvent(GameMember attacker, GameMember victim) {
        super(attacker, victim);
        Stats stats = attacker.getStats();
        setSource("Bot");
        setEmote(Emote.BOT);
        setIsOnHit(true);
        setOnHitScale(BOT_ON_HIT_RATIO);
        addDamage(BOT_DAMAGE);
        addDamage(stats.get(StatType.ATTACK_POWER) * BOT_DAMAGE_AP_RATIO);
        addDamage(stats.get(StatType.SKILL_POWER) * BOT_DAMAGE_SP_RATIO);
    }
}

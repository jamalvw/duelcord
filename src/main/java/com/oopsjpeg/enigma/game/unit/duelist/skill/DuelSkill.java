package com.oopsjpeg.enigma.game.unit.duelist.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.buff.TauntDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

public class DuelSkill extends Skill {
    public static final float DAMAGE_AP_RATIO = 0.6f;
    public static final float DAMAGE_SP_RATIO = 0.65f;
    public static final float ON_HIT_SCALE = 0.5f;
    public static final float TAUNT_CHANCE = 0.5f;

    public DuelSkill(Unit unit) {
        super(unit, 50, 3);
    }

    @Override
    public String act(GameMember actor) {
        GameMember victim = actor.getGame().getRandomTarget(actor);
        List<String> output = new ArrayList<>();

        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Duel**!");

        for (int i = 0; i < 3; i++) {
            Stats stats = actor.getStats();
            DamageEvent e = new DamageEvent(actor, victim);
            e.setSource("Duel");
            e.setEmote(Emote.ATTACK);
            e.setIsOnHit(true);
            e.setOnHitScale(ON_HIT_SCALE);
            e.addDamage(stats.get(StatType.ATTACK_POWER) * DAMAGE_AP_RATIO);
            e.addDamage(stats.get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);
            output.add(EventManager.process(e));
        }

        float random = RANDOM.nextFloat();
        if (random < TAUNT_CHANCE) {
            TauntDebuff taunt = new TauntDebuff(victim, actor, 1);
            output.add(victim.addBuff(taunt, Emote.ANGER));
        }

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Duel";
    }

    @Override
    public String getDescription() {
        return "Strike 3 times. Each strike deals __" + percent(DAMAGE_AP_RATIO) + " Attack Power__ + __"
                + percent(DAMAGE_SP_RATIO) + " Skill Power__ and applies On-Hit effects at __" + percent(ON_HIT_SCALE) + "__ power." +
                "\nHas a __" + percent(TAUNT_CHANCE) + "__ chance to Taunt, forcing the enemy to attack at the start of their turn.";
    }

    @Override
    public String getSimpleDescription() {
        return "Strike 3 times, dealing damage and applying On-Hit effects." +
                "\nHas a chance to Taunt, forcing the enemy to attack at the start of their turn.";
    }
}

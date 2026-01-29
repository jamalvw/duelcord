package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.MAX_HEALTH;
import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class DeadeyeSkill extends Skill {
    public static final int COOLDOWN = 3;
    public static final int COST = 50;
    public static final float BULLSEYE_BARRAGE_INCREASE = 0.025f;
    public static final float CHANCE = 0.15f;
    public static final int DAMAGE = 25;
    public static final float AP_RATIO = 0.7f;
    public static final float BULLSEYE_RATIO = 0.2f;

    public DeadeyeSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);

        int barrageShotsFired = 0;
        if (getUnit() instanceof GunslingerUnit)
            barrageShotsFired = ((GunslingerUnit) getUnit()).getBarrageShotsFired();

        Stats stats = actor.getStats();

        DamageEvent event = new DamageEvent(actor, target);
        event.setEmote(Emote.GUN);
        event.setSource("Deadeye");
        event.setIsAbleToCrit(true);
        event.setIsSkill(true);
        List<String> output = new ArrayList<>();

        event.addDamage(DAMAGE);
        event.addDamage(stats.get(ATTACK_POWER) * AP_RATIO);

        boolean jackpot = false;
        float jackpotRand = Util.RANDOM.nextFloat();
        if (jackpotRand <= CHANCE + (barrageShotsFired * BULLSEYE_BARRAGE_INCREASE)) {
            event.addDamage(Math.max(1, (target.getStats().get(MAX_HEALTH) - target.getHealth()) * BULLSEYE_RATIO));
            jackpot = true;
        }

        output.add(EventDispatcher.dispatch(event));
        output.add(0, Emote.SKILL + "**" + actor.getUsername() + "** used **Deadeye**!" + (jackpot ? " **BULLSEYE**!" : ""));

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Deadeye";
    }

    @Override
    public String getDescription() {
        return "Deal __" + DAMAGE + "__ + __" + percent(AP_RATIO) + " Attack Power__." + "\nHas a __"
                + percent(CHANCE) + "__ chance to **Bullseye**, increased by __" + percentRaw(BULLSEYE_BARRAGE_INCREASE)
                + "__ per Barrage shot hit." + "\nBullseye deals __" + percent(BULLSEYE_RATIO)
                + "__ of the target's missing health." + "\nDeadeye can crit.";
    }

    @Override
    public String getSimpleDescription() {
        return "Deal damage, with a chance to **Bullseye**, dealing more damage based on the enemy's missing health.";
    }
}
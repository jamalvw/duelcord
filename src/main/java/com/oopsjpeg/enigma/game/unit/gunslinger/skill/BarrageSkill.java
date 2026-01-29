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
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class BarrageSkill extends Skill {
    public static final int COOLDOWN = 3;
    public static final int COST = 25;
    public static final int SHOTS = 4;
    public static final int DAMAGE = 6;
    public static final float AP_RATIO = 0.2f;
    public static final float SP_RATIO = 0.55f;

    public BarrageSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);
        Stats stats = actor.getStats();

        List<String> output = new ArrayList<>();
        for (int i = 0; i < SHOTS; i++) {
            if (target.isAlive()) {
                DamageEvent event = new DamageEvent(actor, target);
                event.setEmote(Emote.GUN);
                event.setSource("Barrage");
                event.setIsSkill(true);
                event.setIsAbleToCrit(true);
                event.setIsOnHit(true);
                event.setOnHitScale(.4f);
                event.addDamage(stats.get(ATTACK_POWER) * AP_RATIO);
                event.addDamage(stats.get(SKILL_POWER) * SP_RATIO);
                event.queueAction(() -> {
                    if (getUnit() instanceof GunslingerUnit)
                        ((GunslingerUnit) getUnit()).barrageShot();
                });
                output.add(EventDispatcher.dispatch(event));
            }
        }

        output.add(0, Emote.SKILL + "**" + actor.getUsername() + "** used **Barrage**!");

        if (actor.hasGuides() && !actor.getGuides().hasUsedBarrage()) {
            actor.getGuides().usedBarrage();

            if (actor.getEnergy() >= 50)
                output.add("> You still have **" + actor.getEnergy() + "** energy left! Try using **`>attack`** one more time.");
            else
                output.add("> Use **`>end`** to end your turn early and defend.");
        }

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Barrage";
    }

    @Override
    public String getDescription() {
        return "Fire **" + SHOTS + "** shots, each dealing __" + DAMAGE + "__ + __" + percent(AP_RATIO)
                + " Attack Power__ + __" + percent(SP_RATIO) + " Skill Power__." +
                "\nShots can crit and apply on-hit effects at __25%__ power.";
    }

    @Override
    public String getSimpleDescription() {
        return "Fire " + SHOTS + " shots. Each shot applies On-Hit effects and can crit.";
    }
}
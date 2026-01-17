package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.unit.gunslinger.skill.DeadeyeSkill.*;

public class DeadeyeAction extends SkillAction {
    private final GameMember target;

    public DeadeyeAction(Skill skill, GameMember target) {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor) {
        int barrageShotsFired = 0;
        Unit unit = skill.getUnit();
        if (unit instanceof GunslingerUnit)
            barrageShotsFired = ((GunslingerUnit) unit).getBarrageShotsFired();

        Stats stats = actor.getStats();

        DamageEvent event = new DamageEvent(actor, target);
        List<String> output = new ArrayList<>();

        boolean jackpot = false;
        float jackpotRand = Util.RANDOM.nextFloat();
        if (jackpotRand <= CHANCE + (barrageShotsFired * JACKPOT_BARRAGE_INCREASE)) {
            event.damage += Math.max(1, (event.target.getStats().get(MAX_HEALTH) - event.target.getHealth()) * JACKPOT_RATIO);
            jackpot = true;
        } else {
            event.damage += DAMAGE;
            event.damage += stats.get(ATTACK_POWER) * AP_RATIO;
        }

        event = actor.crit(event);
        event = actor.skill(event);

        output.add(actor.damage(event, Emote.GUN, "Deadeye"));
        output.add(0, Emote.SKILL + "**" + actor.getUsername() + "** used **Deadeye**!" + (jackpot ? " **JACKPOT**!" : ""));

        return Util.joinNonEmpty("\n", output);
    }
}
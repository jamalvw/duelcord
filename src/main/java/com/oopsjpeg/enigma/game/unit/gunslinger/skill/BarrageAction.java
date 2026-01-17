package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.unit.gunslinger.skill.BarrageSkill.*;

public class BarrageAction extends SkillAction {
    private final GameMember target;

    public BarrageAction(Skill skill, GameMember target) {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();

        int shotsFired = 0;

        List<String> output = new ArrayList<>();
        for (int i = 0; i < SHOTS; i++)
            if (target.isAlive()) {
                DamageEvent event = new DamageEvent(actor, target);
                event.onHitScale = .25f;
                event.damage += stats.get(ATTACK_POWER) * AP_RATIO;
                event.damage += stats.get(SKILL_POWER) * SP_RATIO;
                event = actor.skill(event);
                event = actor.crit(event);
                event = actor.hit(event);

                if (!event.cancelled)
                    shotsFired++;

                output.add(actor.damage(event, Emote.GUN, "Barrage"));
            }

        if (skill.getUnit() instanceof GunslingerUnit)
            ((GunslingerUnit) skill.getUnit()).barrageShot();

        output.add(0, Emote.SKILL + "**" + actor.getUsername() + "** used **Barrage**!");

        return Util.joinNonEmpty("\n", output);
    }
}
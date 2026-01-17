package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.gunslinger.buff.RollingBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.game.unit.gunslinger.skill.RollSkill.DODGE;
import static com.oopsjpeg.enigma.game.unit.gunslinger.skill.RollSkill.SP_RATIO;
import static com.oopsjpeg.enigma.util.Util.percent;

public class RollAction extends SkillAction {
    public RollAction(Skill skill) {
        super(skill);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();

        float dodge = DODGE + (stats.get(SKILL_POWER) * SP_RATIO);

        actor.addBuff(new RollingBuff(actor, dodge), Emote.NINJA);
        actor.setEnergy(0);

        return Emote.NINJA + "**" + actor.getUsername() + "** used **Roll**, gaining __" + percent(dodge) + "__ Dodge!";
    }
}
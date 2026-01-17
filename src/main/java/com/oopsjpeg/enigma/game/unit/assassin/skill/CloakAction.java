package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.assassin.buff.CloakedBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.assassin.skill.CloakSkill.COST;
import static com.oopsjpeg.enigma.game.unit.assassin.skill.CloakSkill.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakAction extends SkillAction {
    public CloakAction(Skill skill) {
        super(skill);
    }

    @Override
    public String act(GameMember actor) {
        actor.addBuff(new CloakedBuff(actor, DODGE), Emote.NINJA);
        actor.setEnergy(0);

        return Emote.NINJA + "**" + actor.getUsername() + "** used **Cloak**, gaining __" + percent(DODGE) + "__ Dodge until damaged by a Skill.";
    }
}

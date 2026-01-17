package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.SkillAction;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.assassin.buff.MarkedDebuff;

import static com.oopsjpeg.enigma.game.unit.assassin.skill.MarkSkill.COST;

public class MarkAction extends SkillAction {
    private final GameMember target;

    public MarkAction(Skill skill, GameMember target)
    {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor)
    {
        target.addBuff(new MarkedDebuff(actor), ":bangbang: ");
        return ":bangbang: **" + actor.getUsername() + "** used **Mark** on **" + target.getUsername() + "**.";
    }
}

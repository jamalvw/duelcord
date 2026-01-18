package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.SkillAction;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;
import com.oopsjpeg.enigma.game.unit.reaver.buff.InfiniteBuff;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InfiniteAction extends SkillAction {
    public InfiniteAction(Skill skill) {
        super(skill);
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();
        output.add(actor.addBuff(new InfiniteBuff(actor, 2, 5 + (actor.getStats().get(StatType.SKILL_POWER) * 0.1f), 0.6f), ":infinity: "));
        if (skill.getUnit() instanceof ReaverUnit)
            output.add(((ReaverUnit) skill.getUnit()).skillUsed(actor));
        return Util.joinNonEmpty("\n", output);
    }
}

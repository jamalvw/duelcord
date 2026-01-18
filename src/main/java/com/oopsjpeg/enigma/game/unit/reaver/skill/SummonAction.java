package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.SkillAction;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.reaver.creature.ReaverSummon;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SummonAction extends SkillAction {
    private final int voidPower;

    public SummonAction(Skill skill, int voidPower) {
        super(skill);
        this.voidPower = voidPower;
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();

        for (int i = 0; i < 1 + voidPower; i++)
        {
            ReaverSummon summon = new ReaverSummon(10 + (actor.getStats().get(StatType.ATTACK_POWER) * 0.2f) + (actor.getStats().get(StatType.SKILL_POWER) * 0.6f));
            output.add(actor.addSummon(summon, ":space_invader: "));
        }

        return Util.joinNonEmpty("\n", output);
    }
}

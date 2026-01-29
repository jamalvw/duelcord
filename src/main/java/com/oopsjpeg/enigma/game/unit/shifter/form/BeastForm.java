package com.oopsjpeg.enigma.game.unit.shifter.form;

import com.oopsjpeg.enigma.game.UnitForm;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.skill.beast.BraceSkill;
import com.oopsjpeg.enigma.game.unit.shifter.skill.beast.RoarSkill;
import com.oopsjpeg.enigma.game.unit.shifter.skill.beast.SlamSkill;
import discord4j.rest.util.Color;

public class BeastForm extends UnitForm {
    private final SlamSkill slam = new SlamSkill(getUnit());
    private final RoarSkill roar = new RoarSkill(getUnit());
    private final BraceSkill brace = new BraceSkill(getUnit());

    public BeastForm(Unit unit) {
        super(unit, "Beast Form", Color.DARK_GOLDENROD);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{slam, roar, brace};
    }

    public SlamSkill getSlam() {
        return slam;
    }

    public RoarSkill getRoar() {
        return roar;
    }

    public BraceSkill getBrace() {
        return brace;
    }
}

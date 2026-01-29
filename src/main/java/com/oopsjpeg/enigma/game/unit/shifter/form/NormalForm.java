package com.oopsjpeg.enigma.game.unit.shifter.form;

import com.oopsjpeg.enigma.game.UnitForm;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.skill.normal.LeapSkill;
import com.oopsjpeg.enigma.game.unit.shifter.skill.normal.ZoomSkill;
import discord4j.rest.util.Color;

public class NormalForm extends UnitForm {
    private final ZoomSkill zoom = new ZoomSkill(getUnit());
    private final LeapSkill leap = new LeapSkill(getUnit());

    public NormalForm(Unit unit) {
        super(unit, "Normal Form", Color.CYAN);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{zoom, leap};
    }

    public ZoomSkill getZoom() {
        return zoom;
    }

    public LeapSkill getLeap() {
        return leap;
    }
}

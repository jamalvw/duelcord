package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

public class InjectSkill extends Skill {
    public InjectSkill(Unit unit) {
        super(unit, 25);
    }

    @Override
    public String act(GameMember actor) {
        return "";
    }

    @Override
    public String getName() {
        return "Inject";
    }

    @Override
    public String getDescription() {
        return "";
    }
}

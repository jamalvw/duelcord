package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

public class OverloadSkill extends Skill {
    public OverloadSkill(Unit unit) {
        super(unit, 75);
    }

    @Override
    public String act(GameMember actor) {
        return "";
    }

    @Override
    public String getName() {
        return "Overload";
    }

    @Override
    public String getDescription() {
        return "";
    }
}

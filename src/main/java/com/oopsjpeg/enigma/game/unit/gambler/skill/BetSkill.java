package com.oopsjpeg.enigma.game.unit.gambler.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

public class BetSkill extends Skill {
    public BetSkill(Unit unit) {
        super(unit, 25, 3);
    }

    @Override
    public String act(GameMember actor) {
        return "";
    }

    @Override
    public String getName() {
        return "Bet";
    }

    @Override
    public String getDescription() {
        return "Both players bet __5% current health__.";
    }

    @Override
    public String getSimpleDescription() {
        return "";
    }
}

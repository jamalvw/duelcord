package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;
import com.oopsjpeg.enigma.game.unit.reaver.creature.ReaverSummon;

import java.util.ArrayList;
import java.util.List;

public class SummonSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;

    public SummonSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor) {
        int voidPower = 2;
        if (getUnit() instanceof ReaverUnit)
            voidPower = ((ReaverUnit) getUnit()).getVoidPower();
        return new SummonAction(this, voidPower);
    }

    @Override
    public String getName() {
        return "Summon";
    }

    @Override
    public String getDescription() {
        return "Summon a creature that last for 3 turns. Consumes Void Power to summon up to 2 more.\nCreatures deal 5 + 10% SP damage when I attack. If I take damage, they take damage as well.";
    }
}

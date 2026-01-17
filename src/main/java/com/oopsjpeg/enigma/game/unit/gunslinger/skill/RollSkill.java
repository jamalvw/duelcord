package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class RollSkill extends Skill {
    public static final int COST = 0;
    public static final float DODGE = 0.35f;
    public static final float SP_RATIO = 0.0025f;
    public static final int COOLDOWN = 4;

    public RollSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember member) {
        return new RollAction(this);
    }

    @Override
    public String getName() {
        return "Roll";
    }

    @Override
    public String getDescription() {
        return "End the turn and gain __" + percent(DODGE) + "__ + __" + percentRaw(SP_RATIO) + " Skill Power__ Dodge.";
    }
}
package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakSkill extends Skill {
    public static final int COST = 50;
    public static final int COOLDOWN = 4;
    public static final float DODGE = .8f;

    public CloakSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor)
    {
        return new CloakAction(this);
    }

    @Override
    public String getName() {
        return "Cloak";
    }

    @Override
    public String getDescription() {
        return "End the turn and gain __" + percent(DODGE) + " Dodge__ until your next turn.\n" +
                "Enemy damaging Skills end this effect.";
    }
}

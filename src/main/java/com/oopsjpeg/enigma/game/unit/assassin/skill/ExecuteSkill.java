package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class ExecuteSkill extends Skill {
    public static final int COST = 75;
    public static final int COOLDOWN = 6;
    public static final int DAMAGE_BASE = 40;
    public static final float DAMAGE_MISSING_HP = .04f;
    public static final float DAMAGE_PER_DEBUFF = .065f;

    public ExecuteSkill(Unit unit)
    {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName()
    {
        return "Execute";
    }

    @Override
    public String getDescription()
    {
        return "Deal __" + DAMAGE_BASE + "__ + __" + percent(DAMAGE_MISSING_HP) +
                "__ of enemy missing health, increased by __" + percentRaw(DAMAGE_PER_DEBUFF) + "__ per debuff they have.";
    }

    @Override
    public GameAction act(GameMember actor)
    {
        return new ExecuteAction(this, actor.getGame().getRandomTarget(actor));
    }
}

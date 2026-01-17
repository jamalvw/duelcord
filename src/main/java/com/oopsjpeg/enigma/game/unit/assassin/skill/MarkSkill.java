package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class MarkSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 4;
    public static final float CRIPPLE = .35f;

    public MarkSkill(Unit unit)
    {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName()
    {
        return "Mark";
    }

    @Override
    public String getDescription()
    {
        return "Mark the enemy. If they're still marked on your next turn, consume the mark to Cripple them by __" + percent(CRIPPLE) + "__.";
    }

    @Override
    public GameAction act(GameMember actor)
    {
        return new MarkAction(this, actor.getGame().getRandomTarget(actor));
    }
}

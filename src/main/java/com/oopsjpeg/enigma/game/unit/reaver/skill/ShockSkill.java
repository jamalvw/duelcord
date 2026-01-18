package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class ShockSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;

    public static final int DAMAGE = 10;
    public static final float DAMAGE_SP_RATIO = 0.15f;

    public ShockSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor) {
        return new ShockAction(this, actor.getGame().getRandomTarget(actor));
    }

    @Override
    public String getName() {
        return "Shock";
    }

    @Override
    public String getDescription() {
        return "Shock the enemy, dealing __" + DAMAGE + "__ + __" + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage reducing their energy by 25 on their next turn.";
    }
}

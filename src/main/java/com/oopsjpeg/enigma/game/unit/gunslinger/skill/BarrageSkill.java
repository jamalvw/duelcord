package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BarrageSkill extends Skill {
    public static final int COOLDOWN = 3;
    public static final int COST = 25;
    public static final int SHOTS = 4;
    public static final int DAMAGE = 6;
    public static final float AP_RATIO = 0.25f;
    public static final float SP_RATIO = 0.30f;

    public BarrageSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor) {
        return new BarrageAction(this, actor.getGame().getRandomTarget(actor));
    }

    @Override
    public String getName() {
        return "Barrage";
    }

    @Override
    public String getDescription() {
        return "Fire **" + SHOTS + "** shots, each dealing __" + DAMAGE + "__ + __" + percent(AP_RATIO)
                + " Attack Power__ + __" + percent(SP_RATIO) + " Skill Power__." +
                "\nShots can crit and apply on-hit effects at __25%__ power.";
    }
}
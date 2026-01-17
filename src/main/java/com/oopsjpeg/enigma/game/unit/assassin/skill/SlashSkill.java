package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class SlashSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 2;
    public static final int DAMAGE_BASE = 15;
    public static final float DAMAGE_AP_RATIO = .20f;
    public static final float DAMAGE_SP_RATIO = .55f;
    public static final float BLEED_CHANCE = .2f;
    public static final int BLEED_TURNS = 2;
    public static final float BLEED_DAMAGE_RATIO = .2f;

    public SlashSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName()
    {
        return "Slash";
    }

    @Override
    public String getDescription()
    {
        return "Deal __" + DAMAGE_BASE + "__ + __" + percent(DAMAGE_AP_RATIO) + " AP__ + __"
                + percent(DAMAGE_SP_RATIO) + " SP__.\n" + "Has a __" + percent(BLEED_CHANCE)
                + "__ chance to Bleed for __" + percent(BLEED_DAMAGE_RATIO) + "__ of damage dealt over **"
                + BLEED_TURNS + "** turns.";
    }

    @Override
    public GameAction act(GameMember actor)
    {
        return new SlashAction(this, actor.getGame().getRandomTarget(actor));
    }
}

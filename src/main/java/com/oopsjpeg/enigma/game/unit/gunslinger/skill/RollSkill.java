package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gunslinger.buff.RollingBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class RollSkill extends Skill {
    public static final int COST = 0;
    public static final float DODGE = 0.35f;
    public static final float SP_RATIO = 0.0045f;
    public static final int COOLDOWN = 3;

    public RollSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();

        float dodge = DODGE + (stats.get(SKILL_POWER) * SP_RATIO);

        actor.addBuff(new RollingBuff(actor, actor, dodge), Emote.NINJA);
        actor.setEnergy(0);

        return Emote.NINJA + "**" + actor.getUsername() + "** used **Roll**, gaining __" + percent(dodge) + "__ Dodge!";
    }

    @Override
    public String getName() {
        return "Roll";
    }

    @Override
    public String getDescription() {
        return "End the turn early, gaining __" + percent(DODGE) + "__ + __" + percentRaw(SP_RATIO) + " Skill Power__ Dodge until your next turn.";
    }

    @Override
    public String getSimpleDescription() {
        return "End the turn early, gaining bonus __Dodge Chance__ until your next turn.";
    }
}
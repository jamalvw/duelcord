package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.assassin.buff.CloakedBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;
    public static final float DODGE = .9f;

    public CloakSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        actor.addBuff(new CloakedBuff(actor, actor, DODGE), Emote.NINJA);
        actor.setEnergy(0);

        return Emote.NINJA + "**" + actor.getUsername() + "** used **Cloak**, gaining __" + percent(DODGE) + "__ Dodge until damaged by a Skill.";
    }

    @Override
    public String getName() {
        return "Cloak";
    }

    @Override
    public String getDescription() {
        return "End the turn early and gain __" + percent(DODGE) + " Dodge__ until your next turn.\n" +
                "Being damaged by a skill ends this effect.";
    }
}

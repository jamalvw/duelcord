package com.oopsjpeg.enigma.game.unit.duelist.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.duelist.buff.ParryBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class ParrySkill extends Skill {
    public static final float SKILL_RESIST = 0.4f;
    public static final float BLOCK_CHANCE = 0.5f;

    public ParrySkill(Unit unit) {
        super(unit, 25, 2);
    }

    @Override
    public String act(GameMember actor) {
        ParryBuff parry = new ParryBuff(actor, actor, 2, SKILL_RESIST, BLOCK_CHANCE);
        actor.addBuff(parry, Emote.SHIELD);
        actor.setEnergy(0);
        return Emote.SKILL + "**" + actor.getUsername() + "** used **Parry**, gaining __" + percent(SKILL_RESIST)
                + " Skill Resist__ and __" + percent(BLOCK_CHANCE) + " Block Chance__.";
    }

    @Override
    public String getName() {
        return "Parry";
    }

    @Override
    public String getDescription() {
        return "End the turn early, gaining __" + percent(SKILL_RESIST) + "__ Skill Resist. When attacked, __"
        + percent(BLOCK_CHANCE) + "__ chance to block it and gain 25 bonus energy next turn.";
    }
}

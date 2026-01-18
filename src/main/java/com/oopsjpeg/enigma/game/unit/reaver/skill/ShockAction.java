package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.SkillAction;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.buff.ShockDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.unit.reaver.skill.ShockSkill.DAMAGE;
import static com.oopsjpeg.enigma.game.unit.reaver.skill.ShockSkill.DAMAGE_SP_RATIO;

public class ShockAction extends SkillAction {
    private final GameMember target;

    public ShockAction(Skill skill, GameMember target) {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();
        DamageEvent event = new DamageEvent(actor, target);

        event.damage = DAMAGE + actor.getStats().get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO;

        output.add(target.addBuff(new ShockDebuff(actor, 1, 25), Emote.ZAP));
        output.add(target.damage(event, Emote.ZAP, "Shock"));

        if (skill.getUnit() instanceof ReaverUnit)
            output.add(((ReaverUnit) skill.getUnit()).skillUsed(actor));

        return Util.joinNonEmpty("\n", output);
    }
}

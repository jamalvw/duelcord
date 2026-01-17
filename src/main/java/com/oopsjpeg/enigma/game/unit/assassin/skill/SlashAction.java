package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.buff.BleedingDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.unit.assassin.skill.SlashSkill.*;

public class SlashAction extends SkillAction {
    private final GameMember target;

    public SlashAction(Skill skill, GameMember target) {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();
        List<String> output = new ArrayList<>();
        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Slash**!");

        DamageEvent event = new DamageEvent(actor, target);
        event.damage += DAMAGE_BASE;
        event.damage += stats.get(ATTACK_POWER) * DAMAGE_AP_RATIO;
        event.damage += stats.get(SKILL_POWER) * DAMAGE_SP_RATIO;
        event = actor.skill(event);

        output.add(actor.damage(event, Emote.KNIFE, "Slash"));

        if (!event.cancelled)
        {
            float rand = Util.RANDOM.nextFloat();
            if (rand <= BLEED_CHANCE)
            {
                float bleedDamage = event.damage * BLEED_DAMAGE_RATIO;
                output.add(target.addBuff(new BleedingDebuff(actor, BLEED_TURNS, bleedDamage), Emote.BLEED));
            }
        }

        return Util.joinNonEmpty("\n", output);
    }
}

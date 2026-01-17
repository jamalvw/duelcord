package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.SkillAction;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.assassin.skill.ExecuteSkill.*;

public class ExecuteAction extends SkillAction {
    private final GameMember target;

    public ExecuteAction(Skill skill, GameMember target)
    {
        super(skill);
        this.target = target;
    }

    @Override
    public String act(GameMember actor)
    {
        DamageEvent event = new DamageEvent(actor, target);

        event.damage += DAMAGE_BASE;
        event.damage += DAMAGE_MISSING_HP * target.getMissingHealth();

        target.getBuffs().stream()
                .filter(Buff::isDebuff)
                .forEach(debuff -> event.bonus += DAMAGE_PER_DEBUFF * target.getMissingHealth());

        actor.skill(event);

        return Emote.SKILL + "**" + actor.getUsername() + "** used **Execute**!\n" + actor.damage(event, Emote.KNIFE, "Execute");
    }
}

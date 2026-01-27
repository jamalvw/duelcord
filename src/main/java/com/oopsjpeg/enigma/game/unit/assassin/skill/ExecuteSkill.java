package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class ExecuteSkill extends Skill {
    public static final int COST = 75;
    public static final int COOLDOWN = 5;
    public static final int DAMAGE_BASE = 20;
    public static final float DAMAGE_MISSING_HP = .1f;
    public static final float DAMAGE_PER_DEBUFF = .06f;

    public ExecuteSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName() {
        return "Execute";
    }

    @Override
    public String getDescription() {
        return "Deal __" + DAMAGE_BASE + "__ + __" + percent(DAMAGE_MISSING_HP) +
                "__ of enemy missing health, increased by __" + percentRaw(DAMAGE_PER_DEBUFF) + "__ per debuff they have.";
    }

    @Override
    public String getSimpleDescription() {
        return "Deal damage based on enemy missing health, increased by each debuff they have.";
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);

        DamageEvent e = new DamageEvent(actor, target);

        e.setEmote(Emote.KNIFE);
        e.setSource("Execute");
        e.setIsSkill(true);
        e.addDamage(DAMAGE_BASE);
        e.addDamage(DAMAGE_MISSING_HP * target.getMissingHealth());

        target.getBuffs().stream()
                .filter(Buff::isDebuff)
                .forEach(debuff -> e.addDamage(DAMAGE_PER_DEBUFF * target.getMissingHealth()));

        return Emote.SKILL + "**" + actor.getUsername() + "** used **Execute**!\n" + EventDispatcher.dispatch(e);
    }
}

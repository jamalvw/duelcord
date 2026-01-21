package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BreachSkill extends Skill {
    public static final int DAMAGE = 10;
    public static final float DAMAGE_AP_RATIO = 0.3f;
    public static final float DAMAGE_SP_RATIO = 0.7f;

    public BreachSkill(Unit unit) {
        super(unit, 50);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();
        DamageEvent e = new DamageEvent(actor, actor.getGame().getRandomTarget(actor));
        e.setIsSkill(true);
        e.setIgnoreShield(true);
        e.setEmote(Emote.DIAMOND);
        e.setSource("Breach");

        e.setDamage(DAMAGE);
        e.addDamage(stats.get(StatType.ATTACK_POWER) * DAMAGE_AP_RATIO);
        e.addDamage(stats.get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);

        return EventManager.process(e);
    }

    @Override
    public String getName() {
        return "Breach";
    }

    @Override
    public String getDescription() {
        return "Deal __" + DAMAGE + "__ + __" + percent(DAMAGE_AP_RATIO) + " Attack Power__ + __"
                + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage directly to the enemy’s health, ignoring shields.";
    }
}

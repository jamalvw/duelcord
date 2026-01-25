package com.oopsjpeg.enigma.game.unit.shifter.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;

public class SlamSkill extends Skill {
    public static final int DAMAGE = 20;
    public static final float DAMAGE_AP_RATIO = 0.8f;
    public static final float DAMAGE_SP_RATIO = 0.2f;

    public static final float HEAL = 0.25f;

    public SlamSkill(Unit unit) {
        super(unit, 25, 2);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();
        GameMember target = actor.getGame().getRandomTarget(actor);

        DamageEvent e = new DamageEvent(actor, target);
        e.setSource("Slam");
        e.addDamage(DAMAGE);
        e.addDamage(stats.get(StatType.ATTACK_POWER) * DAMAGE_AP_RATIO);
        e.addDamage(stats.get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);
        e.proposeEffect(() -> {
            if (target.hasBuff(WeakenedDebuff.class))
                e.getOutput().add(target.heal(e.getDamage() * HEAL, "Slam"));
        });

        return EventManager.process(e);
    }

    @Override
    public String getName() {
        return "Slam";
    }

    @Override
    public String getDescription() {
        return "**Beast**: Deal __" + DAMAGE + "__ + __" + percent(DAMAGE_AP_RATIO) + "__ + __" + percent(DAMAGE_SP_RATIO) + " Skill Power>e__ damage."
                + "\nIf the enemy is weakened, heal for __" + percent(HEAL) + "__ of the damage dealt.";
    }

    @Override
    public String getSimpleDescription() {
        return "";
    }
}

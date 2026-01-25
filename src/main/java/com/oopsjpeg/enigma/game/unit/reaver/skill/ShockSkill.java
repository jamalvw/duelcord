package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.EventManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.buff.ShockDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.percent;

public class ShockSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;

    public static final int DAMAGE = 10;
    public static final float DAMAGE_SP_RATIO = 0.15f;

    public ShockSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);
        List<String> output = new ArrayList<>();

        DamageEvent e = new DamageEvent(actor, target);
        e.setEmote(Emote.ZAP);
        e.setSource("Shock");
        e.setIsSkill(true);
        e.setDamage(DAMAGE + actor.getStats().get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);
        e.proposeEffect(() -> e.getOutput().add(target.addBuff(new ShockDebuff(target, actor, 1, 50), Emote.ZAP)));

        output.add(EventManager.process(e));

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Shock";
    }

    @Override
    public String getDescription() {
        return "Shock the enemy, dealing __" + DAMAGE + "__ + __" + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage reducing their energy by 50 on their next turn.";
    }

    @Override
    public String getSimpleDescription() {
        return "Shock the enemy, dealing damage and reducing their energy by 50 on their next turn.";
    }
}

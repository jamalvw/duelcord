package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.ShieldEvent;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.hacker.buff.FirewallBuff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class FirewallSkill extends Skill {
    public static final int SHIELD = 35;
    public static final float SHIELD_AP_RATIO = 0.1f;
    public static final float SHIELD_SP_RATIO = 0.45f;

    public FirewallSkill(Unit unit) {
        super(unit, 25, 3);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();
        actor.addBuff(new FirewallBuff(actor, actor, 1, 2), Emote.BUFF);
        actor.setEnergy(0);
        // TODO: When ShieldEvent is made, use that instead
        ShieldEvent e = new ShieldEvent(actor, SHIELD);
        e.setSource("Firewall");
        e.addAmount(stats.get(ATTACK_POWER) * SHIELD_AP_RATIO);
        e.addAmount(stats.get(SKILL_POWER) * SHIELD_SP_RATIO);
        return EventDispatcher.dispatch(e);
    }

    @Override
    public String getName() {
        return "Firewall";
    }

    @Override
    public String getDescription() {
        return "End the turn early and shield for __" + SHIELD + "__ + __" + percent(SHIELD_AP_RATIO)
                + " Attack Power__ + __" + percent(SHIELD_SP_RATIO) + " Skill Power__."
                + "\nIf the shield breaks, create **2 Bots**.";
    }

    @Override
    public String getSimpleDescription() {
        return "End the turn early and gain a shield. If the shield breaks, create **2 Bots**.";
    }
}

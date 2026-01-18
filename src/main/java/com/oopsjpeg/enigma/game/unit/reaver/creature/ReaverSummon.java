package com.oopsjpeg.enigma.game.unit.reaver.creature;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.object.Summon;
import com.oopsjpeg.enigma.util.Emote;

public class ReaverSummon extends Summon {
    public ReaverSummon(float health) {
        super("Creature", health, false);
    }

    @Override
    public DamageEvent damageIn(DamageEvent event) {
        float total = event.total() * 1 - event.target.getResist();
        takeHealth(total);
        if (getHealth() <= 0) remove();
        return event;
    }

    @Override
    public DamageEvent attackOut(DamageEvent e) {
        DamageEvent event = new DamageEvent(e.actor, e.target);

        event.damage += event.actor.getStats().get(StatType.ATTACK_POWER) * 0.1f;
        event.damage += event.actor.getStats().get(StatType.SKILL_POWER) + 0.3f;

        e.output.add(event.target.damage(event, ":space_invader: ", getName()));

        return e;
    }
}

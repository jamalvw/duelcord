package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakedBuff extends Buff {
    public CloakedBuff(GameMember source, float power) {
        super("Cloaked", false, source, 2, power);
    }

    @Override
    public DamageEvent skillIn(DamageEvent event) {
        if (!event.cancelled) remove();
        return event;
    }

    @Override
    public String getStatus(GameMember member) {
        return "Cloaked: " + percent(getPower()) + " bonus Dodge until damaged by Skill";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(DODGE, getPower());
    }
}
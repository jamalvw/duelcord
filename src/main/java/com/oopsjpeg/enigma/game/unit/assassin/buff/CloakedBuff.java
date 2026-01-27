package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakedBuff extends Buff {
    public CloakedBuff(GameMember owner, GameMember source, float power) {
        super(owner, source, "Cloak", false, 2, false, power);

        hook(EventType.DAMAGE_RECEIVED, Priority.VALIDATION, (Consumer<DamageEvent>) e -> {
            if (e.getVictim() != getOwner()) return;
            if (!e.isSkill()) return;

            e.queueAction(this::remove);
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Cloaked: " + percent(getPower()) + " dodge chance until damaged by a skill";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(DODGE, getPower());
    }
}
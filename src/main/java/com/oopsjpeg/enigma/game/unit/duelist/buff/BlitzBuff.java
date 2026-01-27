package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_COST;

public class BlitzBuff extends Buff {
    public BlitzBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Blitz", false, totalTurns, true, power);

        hook(EventType.DAMAGE_DEALT, Priority.POST_DAMAGE, (Consumer<DamageEvent>) event -> {
            event.multiplyDamage(getPower());
        });
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(ATTACK_COST, -25);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Blitz: Attacks cost 25";
    }

    @Override
    public String onTurnEnd(GameMember member) {
        remove(true);
        return "";
    }
}

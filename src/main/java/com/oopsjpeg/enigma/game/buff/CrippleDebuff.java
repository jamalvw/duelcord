package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CrippleDebuff extends Buff {
    public CrippleDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Cripple", true, totalTurns, false, power);

        hook(EventType.DAMAGE_DEALT, Priority.POST_DAMAGE, (Consumer<DamageEvent>) event -> {
            if (event.getVictim() != getOwner()) return;

            event.multiplyDamage(1 + getPower());
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Crippled: Taking " + formatPower() + " more damage";
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }
}

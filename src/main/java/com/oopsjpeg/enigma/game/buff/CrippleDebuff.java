package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CrippleDebuff extends Buff {
    public CrippleDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Cripple", true, totalTurns, false, power);

        onDamageDealt(Priority.POST_DAMAGE, event -> {
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

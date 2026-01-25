package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CrippleDebuff extends Buff {
    public CrippleDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Cripple", true, totalTurns, false, power);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.POST_DAMAGE;
            }

            @Override
            public void execute(DamageEvent event) {
                if (event.getVictim() != getOwner()) return;

                event.multiplyDamage(1 + getPower());
            }
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

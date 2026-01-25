package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WeakenedDebuff extends Buff {
    public WeakenedDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Weaken", true, totalTurns, true, power);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.RESISTANCE;
            }

            @Override
            public void execute(DamageEvent event) {
                if (event.getActor() != getOwner()) return;

                event.multiplyDamage(1 - getPower());
            }
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Weakened: Deals " + percent(getPower()) + " less damage";
    }

    @Override
    public String onTurnStart(GameMember member) {
        return Emote.WEAKEN + "**" + member.getUsername() + "** deals __" + formatPower() + "__ less damage this turn.";
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }
}

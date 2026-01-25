package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.HealEvent;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WoundedDebuff extends Buff {
    public WoundedDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Wound", true, totalTurns, true, power);

        hook(HealEvent.class, new Hook<HealEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.POST_DAMAGE;
            }

            @Override
            public void execute(HealEvent e) {
                e.multiplyAmount(1 - getPower());
            }
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Wounded: Receives " + formatPower() + " less healing";
    }

    @Override
    public String onTurnStart(GameMember member) {
        return Emote.WOUND + "**" + member.getUsername() + "** receives __" + formatPower() + "__ less healing this turn.";
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }
}

package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakedBuff extends Buff {
    public CloakedBuff(GameMember owner, GameMember source, float power) {
        super(owner, source, "Cloak", false, 2, false, power);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.VALIDATION;
            }

            @Override
            public void execute(DamageEvent e) {
                if (e.getVictim() != getOwner()) return;
                if (!e.isSkill()) return;

                e.proposeEffect(() -> remove());
            }
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
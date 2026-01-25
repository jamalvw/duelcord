package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;

import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit.*;

public class TracingBuff extends Buff {
    public TracingBuff(GameMember owner, GameMember source) {
        super(owner, source, "Tracing", false, 1, true, 0);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.PRE_CALCULATION;
            }

            @Override
            public void execute(DamageEvent e) {
                GameMember actor = e.getActor();

                if (actor != getOwner()) return;

                Stats stats = actor.getStats();
                e.addDamage(PASSIVE_DAMAGE_BASE);
                // e.addDamage(stats.get(ATTACK_POWER) * PASSIVE_DAMAGE_AP_RATIO);
                e.addDamage(stats.get(SKILL_POWER) * PASSIVE_DAMAGE_SP_RATIO);

                e.proposeEffect(() -> {
                    actor.giveEnergy(PASSIVE_ENERGY_RESTORE);

                    if (actor.getUnit() instanceof AssassinUnit) {
                        ((AssassinUnit) actor.getUnit()).getSlash().getCooldown().reset();
                    }

                    remove(true);
                });
            }
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Tracing: " + getTotalDamage(member.getStats()) + " bonus damage on attack, restores " + PASSIVE_ENERGY_RESTORE + " energy, resets Slash";
    }

    public int getTotalDamage(Stats stats) {
        return Math.round(PASSIVE_DAMAGE_BASE +
                // (stats.get(ATTACK_POWER) * PASSIVE_DAMAGE_AP_RATIO) +
                (stats.get(SKILL_POWER) * PASSIVE_DAMAGE_SP_RATIO));
    }
}
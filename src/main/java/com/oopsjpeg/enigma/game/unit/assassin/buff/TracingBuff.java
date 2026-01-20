package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit.*;

public class TracingBuff extends Buff {
    public TracingBuff(GameMember owner, GameMember source) {
        super(owner, source, "Tracing", false, 2, 0);
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.PRE_CALCULATION;
                    }

                    @Override
                    public void execute(DamageEvent e) {
                        if (e.getAttacker() != getOwner()) return;

                        Stats stats = e.getAttacker().getStats();
                        e.addDamage(PASSIVE_DAMAGE_BASE);
                        e.addDamage(stats.get(ATTACK_POWER) * PASSIVE_DAMAGE_AP_RATIO);
                        e.addDamage(stats.get(SKILL_POWER) * PASSIVE_DAMAGE_SP_RATIO);

                        e.proposeEffect(() -> {
                            e.getAttacker().giveEnergy(PASSIVE_ENERGY_RESTORE);

                            if (e.getAttacker().getUnit() instanceof AssassinUnit)
                            {
                                ((AssassinUnit) e.getAttacker().getUnit()).getSlash().getCooldown().reset();
                            }

                            remove(true);
                        });
                    }
                }
        };
    }

    @Override
    public String getStatus(GameMember member) {
        return "Tracing: " + getTotalDamage(member.getStats()) + " bonus on Attack, restore " + PASSIVE_ENERGY_RESTORE + " Energy, reset Slash";
    }

    public int getTotalDamage(Stats stats) {
        return Math.round(PASSIVE_DAMAGE_BASE +
                (stats.get(ATTACK_POWER) * PASSIVE_DAMAGE_AP_RATIO) +
                (stats.get(SKILL_POWER) * PASSIVE_DAMAGE_SP_RATIO));
    }
}
package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit;
import com.oopsjpeg.enigma.util.Cooldown;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.game.unit.assassin.AssassinUnit.*;

public class TracingBuff extends Buff {
    public TracingBuff(GameMember source) {
        super("Tracing", false, source, 1, 0);
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

    @Override
    public DamageEvent attackOut(DamageEvent event) {
        if (!event.cancelled) {
            Stats stats = event.actor.getStats();
            event.bonus += PASSIVE_DAMAGE_BASE;
            event.bonus += stats.get(ATTACK_POWER) * PASSIVE_DAMAGE_AP_RATIO;
            event.bonus += stats.get(SKILL_POWER) * PASSIVE_DAMAGE_SP_RATIO;
            event.actor.giveEnergy(PASSIVE_ENERGY_RESTORE);

            if (event.actor.getUnit() instanceof AssassinUnit)
            {
                ((AssassinUnit) event.actor.getUnit()).getSlash().getCooldown().reset();
            }
        }

        remove(true);

        return event;
    }
}
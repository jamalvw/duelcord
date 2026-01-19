package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.game.StatType.DODGE;

public class DodgeHook implements DamageHook {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.VALIDATION;
    }

    @Override
    public void execute(DamageEvent event) {
        if (event.isSkill()) return;

        GameMember victim = event.getVictim();
        float dodgeChance = victim.getStats().get(DODGE);

        if (dodgeChance > 0)
        {
            float rand = Util.RANDOM.nextFloat();

            if (rand < dodgeChance)
            {
                event.getOutput().add(Emote.DODGE + "**" + event.getVictim().getUsername() + "** dodged the hit!");
                event.setCancelled(true);
            }
        }
    }
}

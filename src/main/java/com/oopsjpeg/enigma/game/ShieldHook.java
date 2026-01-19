package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

public class ShieldHook implements DamageHook {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.SHIELDING;
    }

    @Override
    public void execute(DamageEvent event) {
        GameMember attacker = event.getAttacker();
        GameMember victim = event.getVictim();

        if (victim.hasShield()) {
            float finalDamage = event.getDamage();
            float shield = victim.getShield();

            float damageToShield = Util.limit(finalDamage, 0, shield);

            shield -= damageToShield;
            event.subtractDamage(damageToShield);

            if (shield > 0)
                event.getOutput().add(0, Util.damageText(event, attacker.getUsername(), victim.getUsername() + "'s Shield", event.getEmote(), event.getSource()));
            else
                event.getOutput().add(Emote.DEFEND + "**" + event.getVictim().getUsername() + "'s Shield** was destroyed!");

            event.proposeEffect(() -> victim.takeShield(damageToShield));
        }
    }
}

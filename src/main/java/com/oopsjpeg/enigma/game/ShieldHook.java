package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

public class ShieldHook implements Hook<DamageEvent> {
    @Override
    public DamagePhase getPhase() {
        return DamagePhase.SHIELDING;
    }

    @Override
    public void execute(DamageEvent event) {
        GameMember attacker = event.getActor();
        GameMember victim = event.getVictim();

        if (!event.isIgnoreShield() && victim.hasShield()) {
            float finalDamage = event.getDamage();
            float shield = victim.getShield();

            float damageToShield = Util.limit(finalDamage, 0, shield);

            shield -= damageToShield;

            event.subtractDamage(damageToShield);

            event.proposeEffect(() -> {
                victim.subtractShield(damageToShield);

                if (victim.hasShield())
                    event.getOutput().add(0, Util.damageText(event, attacker.getUsername(), victim.getUsername() + "'s Shield", event.getEmote(), event.getSource()));
                else {
                    event.getOutput().add(Emote.DEFEND + "**" + event.getVictim().getUsername() + "'s Shield** was destroyed!");
                    victim.getData().forEach(o -> event.getOutput().add(o.onShieldBreak(victim)));
                }
            });
        }
    }
}

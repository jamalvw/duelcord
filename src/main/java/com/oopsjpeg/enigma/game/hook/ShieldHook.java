package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

public class ShieldHook extends Hook<DamageEvent> {
    public ShieldHook() {
        super(EventType.DAMAGE_ALL, Priority.SHIELDING, event -> {
            GameMember attacker = event.getActor();
            GameMember victim = event.getVictim();

            if (!event.isIgnoreShield() && victim.hasShield()) {
                float finalDamage = event.getDamage();
                float shield = victim.getShield();

                float damageToShield = Util.limit(finalDamage, 0, shield);

                shield -= damageToShield;

                event.subtractDamage(damageToShield);

                event.queueAction(() -> {
                    victim.subtractShield(damageToShield);

                    if (victim.hasShield())
                        event.getOutput().add(0, Util.damageText(event, attacker.getUsername(), victim.getUsername() + "'s Shield", event.getEmote(), event.getSource()));
                    else {
                        event.getOutput().add(Emote.DEFEND + "**" + event.getVictim().getUsername() + "'s Shield** was destroyed!");
                        victim.getData().forEach(o -> event.getOutput().add(o.onShieldBreak(victim)));
                    }
                });
            }
        });
    }
}

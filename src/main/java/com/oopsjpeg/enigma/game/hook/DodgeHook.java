package com.oopsjpeg.enigma.game.hook;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.game.StatType.DODGE;

public class DodgeHook extends Hook<DamageEvent> {
    public DodgeHook() {
        super(EventType.DAMAGE_ALL, Priority.VALIDATION, event -> {
            if (!event.isOnHit()) return;

            GameMember victim = event.getVictim();
            float dodgeChance = victim.getStats().get(DODGE);

            if (dodgeChance > 0) {
                float rand = Util.RANDOM.nextFloat();

                if (rand < dodgeChance) {
                    event.getOutput().add(Emote.DODGE + "**" + event.getVictim().getUsername() + "** dodged **" + event.getActor().getUsername() + "**'s hit!");
                    event.cancel();
                }
            }
        });
    }
}

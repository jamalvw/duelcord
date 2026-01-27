package com.oopsjpeg.enigma.game.unit.reaver.creature;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Summon;
import com.oopsjpeg.enigma.util.Emote;

public class ReaverSummon extends Summon {
    private final float damage;

    public ReaverSummon(GameMember owner, float health, float damage) {
        super("Creature", owner, health, false);
        this.damage = damage;

        onDamageDealt(Priority.PRE_CALCULATION, e -> {
            if (!e.isAttack()) return;

            DamageEvent summonStrikeEvent = new DamageEvent(getOwner(), e.getVictim());
            summonStrikeEvent.setIsSkill(true);
            summonStrikeEvent.setIsDoT(true);
            summonStrikeEvent.setEmote(Emote.SUMMON);
            summonStrikeEvent.setSource(getName());
            summonStrikeEvent.addDamage(damage);

            e.queueAction(() -> e.getOutput().add(EventDispatcher.dispatch(summonStrikeEvent)));
        });
        onDamageReceived(Priority.SUMMONS, event -> {
            takeHealth(event.getDamage());
            if (getHealth() <= 0) {
                remove();
            }
        });
    }
}

package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.Event;
import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;

import static java.lang.Math.round;

public class WolfbiteEffect extends Effect {
    public WolfbiteEffect(GameMember owner, float power) {
        super(owner, "Wolfbite", power, null);

        onHealSeen(Priority.PRE_CALCULATION, this::doEffect);
        onShieldSeen(Priority.PRE_CALCULATION, this::doEffect);
    }

    @Override
    public String getDescription() {
        return "When the enemy heals or shields, deal __" + round(getPower()) + "__ damage to them.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Wolfbite: " + getPower();
    }

    public void doEffect(Event e) {
        e.queueAction(() -> {
            DamageEvent damage = new DamageEvent(getOwner(), e.getActor());
            damage.setIsDoT(true);
            damage.setDamage(getPower());
            damage.setSource(getName());
            e.getOutput().add(EventDispatcher.dispatch(damage));
        });
    }
}

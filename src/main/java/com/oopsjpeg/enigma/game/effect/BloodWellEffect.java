package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Util;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BloodWellEffect extends Effect {
    private final int maxShield;

    private int currentShield = 0;

    public BloodWellEffect(GameMember owner, float power, int maxShield) {
        super(owner, "Blood Well", power, null);
        this.maxShield = maxShield;

        hook(EventType.DAMAGE_DEALT, Priority.POST_DAMAGE, (Consumer<DamageEvent>) event -> {
            if (!event.isAttack()) return;

            float shieldAmount = event.getDamage() * getPower();

            shieldAmount = Util.limit(shieldAmount, 0, maxShield - currentShield);

            currentShield += shieldAmount;

            event.addShielding(shieldAmount);
        });
    }

    public int getMaxShield() {
        return maxShield;
    }

    public int getCurrentShield() {
        return currentShield;
    }

    @Override
    public String onTurnStart(GameMember member) {
        currentShield = 0;
        return null;
    }

    @Override
    public String getDescription() {
        return "Attacks shield for __" + percent(getPower()) + "__ of damage dealt, up to **" + maxShield + "**.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Blood Well: " + currentShield + "/" + maxShield;
    }
}

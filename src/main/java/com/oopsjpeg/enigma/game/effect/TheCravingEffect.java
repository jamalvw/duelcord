package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;

import static java.lang.Math.round;

public class TheCravingEffect extends Effect {
    private final int hpThreshold;
    private int amountHealed;

    public TheCravingEffect(GameMember owner, float power, int hpThreshold) {
        super(owner, "The Craving", power, null);
        this.hpThreshold = hpThreshold;

        onHealReceived(Priority.PRE_CALCULATION, e -> {
            e.queueAction(() -> {
                amountHealed += round(e.getAmount());
                if (amountHealed >= hpThreshold) {
                    amountHealed -= hpThreshold;
                    GameMember target = e.getGame().getRandomTarget(e.getActor());
                    DamageEvent damage = new DamageEvent(e.getActor(), target);
                    damage.setDamage(getPower());
                    damage.setIsDoT(true);
                    damage.setSource(getName());
                    e.getOutput().add(EventDispatcher.dispatch(damage));
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return "For every __" + hpThreshold + "__ health you heal, deal __" + round(getPower()) + "__ damage.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Blood Well: " + amountHealed + "/" + hpThreshold;
    }
}

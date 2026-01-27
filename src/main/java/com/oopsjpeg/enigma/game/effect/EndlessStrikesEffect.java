package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class EndlessStrikesEffect extends Effect {
    private int multiplier = 0;

    public EndlessStrikesEffect(GameMember owner, float power) {
        super(owner, "Endless Strikes", power, null);

        hook(EventType.DAMAGE_DEALT, Priority.PRE_CALCULATION, (Consumer<DamageEvent>) event -> {
            if (!event.isOnHit()) return;

            multiplier++;

            event.multiplyDamage(1 + (multiplier * getPower()));
        });
    }

    @Override
    public String onTurnEnd(GameMember member) {
        multiplier = 0;
        return null;
    }

    @Override
    public String getDescription() {
        return "Each Hit deals __" + percent(getPower()) + "__ more than the last for this turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Endless Strikes: " + percent(multiplier * getPower());
    }
}

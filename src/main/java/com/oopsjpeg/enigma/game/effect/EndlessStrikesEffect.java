package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.object.Effect;

import static com.oopsjpeg.enigma.util.Util.percent;

public class EndlessStrikesEffect extends Effect
{
    private int multiplier = 0;

    public EndlessStrikesEffect(GameMember owner, float power)
    {
        super(owner, "Endless Strikes", power, null);

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.PRE_CALCULATION;
            }

            @Override
            public void execute(DamageEvent event) {
                if (event.getActor() != getOwner()) return;
                if (!event.isOnHit()) return;

                multiplier++;

                event.multiplyDamage(1 + (multiplier * getPower()));
            }
        });
    }

    @Override
    public String onTurnEnd(GameMember member)
    {
        multiplier = 0;
        return null;
    }

    @Override
    public String getDescription()
    {
        return "Each Hit deals __" + percent(getPower()) + "__ more than the last for this turn.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Endless Strikes: " + percent(multiplier * getPower());
    }
}

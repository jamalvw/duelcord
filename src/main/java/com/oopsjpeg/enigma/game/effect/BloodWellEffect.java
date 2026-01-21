package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BloodWellEffect extends Effect
{
    private final int maxShield;

    private int currentShield = 0;

    public BloodWellEffect(GameMember owner, float power, int maxShield)
    {
        super(owner, "Blood Well", power, null);
        this.maxShield = maxShield;

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.POST_DAMAGE;
            }

            @Override
            public void execute(DamageEvent event) {
                if (event.getActor() != getOwner()) return;
                if (!event.isAttack()) return;

                float shieldAmount = event.getDamage() * getPower();

                shieldAmount = Util.limit(shieldAmount, 0, maxShield - currentShield);

                currentShield += shieldAmount;

                event.addShielding(shieldAmount);
            }
        });
    }

    public int getMaxShield()
    {
        return maxShield;
    }

    public int getCurrentShield()
    {
        return currentShield;
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        currentShield = 0;
        return null;
    }

    @Override
    public String getDescription()
    {
        return "Attacks shield for __" + percent(getPower()) + "__ of damage dealt, up to **" + maxShield + "**.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Blood Well: " + currentShield + "/" + maxShield;
    }
}

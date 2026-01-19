package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.WoundedDebuff;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import static com.oopsjpeg.enigma.util.Util.percent;

public class LifewasterEffect extends Effect
{
    private final Stacker hitCount;

    public LifewasterEffect(GameMember owner, int hitLimit, float power)
    {
        super(owner, "Lifewaster", power, null);
        hitCount = new Stacker(hitLimit);
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.PRE_CALCULATION;
                    }

                    @Override
                    public void execute(DamageEvent event) {
                        if (event.getAttacker() != getOwner()) return;
                        if (!event.isOnHit()) return;

                        event.proposeEffect(() -> {
                            if (hitCount.stack())
                            {
                                event.getOutput().add(event.getVictim().addBuff(new WoundedDebuff(event.getVictim(), event.getAttacker(), 1, getPower()), Emote.WOUND));
                                hitCount.reset();
                            }
                        });
                    }
                }
        };
    }

    @Override
    public String getDescription()
    {
        return "Every " + hitCount.getMax() + " Hits, **Wound** the target by __" + percent(getPower()) + "__ on their next turn.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Lifewaster: " + hitCount.getCurrent() + "/" + hitCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

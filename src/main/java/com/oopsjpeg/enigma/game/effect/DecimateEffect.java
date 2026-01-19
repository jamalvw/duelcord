package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.CrippledDebuff;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.util.Util.percent;

public class DecimateEffect extends Effect
{
    private final Stacker critCount;

    public DecimateEffect(GameMember owner, int critLimit, float power)
    {
        super(owner, "Decimate", power, null);
        this.critCount = new Stacker(critLimit);
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[] {
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.POST_DAMAGE;
                    }

                    @Override
                    public void execute(DamageEvent event) {
                        if (event.getAttacker() != getOwner()) return;
                        if (!event.isGoingToCrit()) return;

                        event.proposeEffect(() -> {
                            if (critCount.stack())
                            {
                                event.getOutput().add(event.getVictim().addBuff(new CrippledDebuff(event.getVictim(), event.getAttacker(), 1, getPower()), Emote.CRIPPLE));
                                critCount.reset();
                            }
                        });
                    }
                }
        };
    }

    @Override
    public String getDescription()
    {
        return "Every **" + critCount.getMax() + "** Crits, **Cripple** the target by __" + percent(getPower()) + "__ until their next turn.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Decimate: " + critCount.getCurrent() + "/" + critCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

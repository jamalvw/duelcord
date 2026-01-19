package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class CrippledDebuff extends Buff
{
    public CrippledDebuff(GameMember owner, GameMember source, int totalTurns, float power)
    {
        super(owner, source, "Crippled", true, totalTurns, power);
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
                        if (event.getVictim() != getOwner()) return;

                        event.multiplyDamage(1 + getPower());
                    }
                }
        };
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Crippled: Taking " + percent(getPower()) + " more damage";
    }

    @Override
    public String formatPower()
    {
        return percent(getPower());
    }
}

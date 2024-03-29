package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WolfbiteEffect extends Effect
{
    private final Stacker attackCount;

    public WolfbiteEffect(int attackLimit, float power)
    {
        super("Wolfbite", power, null);
        this.attackCount = new Stacker(attackLimit);
    }

    @Override
    public DamageEvent attackOut(DamageEvent event)
    {
        if (attackCount.stack())
        {
            event.output.add(event.target.addBuff(new WeakenedDebuff(event.actor, 1, getPower()), Emote.WEAKEN));
            attackCount.reset();
        }
        return event;
    }

    @Override
    public String getDescription()
    {
        return "Every **" + attackCount.getMax() + "** Attacks, **Weaken** the target by __" + percent(getPower()) + "__ on their next turn.";
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Wolfbite: " + attackCount.getCurrent() + "/" + attackCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

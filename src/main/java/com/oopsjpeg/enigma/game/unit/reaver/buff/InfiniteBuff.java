package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

public class InfiniteBuff extends Buff {
    private float chance;

    public InfiniteBuff(GameMember source, int totalTurns, float power, float chance) {
        super("Infinite", false, source, totalTurns, power);
        this.chance = chance;
    }

    @Override
    public DamageEvent damageOut(DamageEvent event) {
        if (RANDOM.nextFloat() < chance)
        {
            if (!event.target.hasBuff(VoidburnDebuff.class))
                event.output.add(event.target.addBuff(new VoidburnDebuff(event.actor, 2, getPower()), Emote.VOIDBURN));
            else
            {
                VoidburnDebuff burn = (VoidburnDebuff) event.target.getBuff(VoidburnDebuff.class);
                burn.setPower(burn.getPower() + getPower());
                event.output.add(Emote.VOIDBURN + "Voidburn's damage increased to **" + burn.formatPower() + "**!");
            }
        }
        return event;
    }

    @Override
    public String getStatus(GameMember member) {
        return "Infinite: " + percent(chance) + " chance to apply Voidburn";
    }
}

package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.DamageManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BleedingDebuff extends Buff
{
    public BleedingDebuff(GameMember owner, GameMember source, int totalTurns, float power)
    {
        super(owner, source, "Bleeding", true, totalTurns, power);
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Bleeding: Taking " + Math.round(getPower()) + " damage (" + getCurrentTurns() + " turns left)";
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        DamageEvent e = new DamageEvent(getSource(), member);
        e.setEmote(Emote.BLEED);
        e.setSource("Bleed");
        e.setDamage(getPower());
        return DamageManager.process(e);
    }
}

package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WoundedDebuff extends Buff
{
    public WoundedDebuff(GameMember owner, GameMember source, int totalTurns, float power)
    {
        super(owner, source, "Wound", true, totalTurns, true, power);
    }

    @Override
    public String getStatus(GameMember member)
    {
        return "Wounded: Receives " + formatPower() + " less healing";
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        return Emote.WOUND + "**" + member.getUsername() + "** receives __" + formatPower() + "__ less healing this turn.";
    }

    @Override
    public float onHeal(float healAmount)
    {
        return healAmount * (1 - getPower());
    }

    @Override
    public String formatPower()
    {
        return percent(getPower());
    }
}

package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class BleedDebuff extends Buff {
    public BleedDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Bleed", true, totalTurns, false, power);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Bleeding: Taking " + formatPower() + " damage per turn";
    }

    @Override
    public String onTurnStart(GameMember member) {
        DamageEvent e = new DamageEvent(getSource(), member);
        e.setEmote(Emote.BLEED);
        e.setIsDoT(true);
        e.setSource("Bleed");
        e.setDamage(getPower());
        return EventDispatcher.dispatch(e);
    }
}

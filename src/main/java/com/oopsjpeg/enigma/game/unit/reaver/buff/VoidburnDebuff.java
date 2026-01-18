package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class VoidburnDebuff extends Buff {
    public VoidburnDebuff(GameMember source, int totalTurns, float power) {
        super("Voidburn", true, source, totalTurns, power);
    }

    @Override
    public String onTurnStart(GameMember member) {
        DamageEvent event = new DamageEvent(getSource(), member);
        event.damage = getPower();
        return member.damage(event, Emote.VOIDBURN, getName());
    }

    @Override
    public String getStatus(GameMember member) {
        return "Voidburn: Taking " + formatPower() + " damage per turn";
    }
}

package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.EventManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class VoidburnDebuff extends Buff {
    public VoidburnDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Voidburn", true, totalTurns, false, power);
    }

    @Override
    public String onTurnStart(GameMember owner) {
        DamageEvent event = new DamageEvent(getSource(), owner);
        event.setEmote(Emote.VOIDBURN);
        event.setIsSkill(true);
        event.setIsDoT(true);
        event.setSource(getName());
        event.setDamage(getPower());
        return EventManager.process(event);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Voidburn: Taking " + formatPower() + " damage per turn";
    }
}

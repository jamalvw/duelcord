package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.DamageManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class VoidburnDebuff extends Buff {
    public VoidburnDebuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Voidburn", true, totalTurns, power);
    }

    @Override
    public String onTurnStart(GameMember owner) {
        DamageEvent event = new DamageEvent(getSource(), owner);
        event.setEmote(Emote.VOIDBURN);
        event.setIsSkill(true);
        event.setIsDoT(true);
        event.setSource(getName());
        event.setDamage(getPower());
        return DamageManager.process(event);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Voidburn: Taking " + formatPower() + " damage per turn";
    }
}

package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.event.HealEvent;
import com.oopsjpeg.enigma.game.object.Buff;

public class PotionBuff extends Buff {
    public PotionBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Potion Healing", false, totalTurns - 1, false, power / 2);
    }

    @Override
    public String onTurnStart(GameMember member) {
        HealEvent e = new HealEvent(member, getPower() / getTotalTurns());
        return EventDispatcher.dispatch(e);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Potion: Healing " + formatPower() + " per turn";
    }
}

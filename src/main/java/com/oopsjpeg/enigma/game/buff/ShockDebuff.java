package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class ShockDebuff extends Buff {
    public ShockDebuff(GameMember source, int totalTurns, float power) {
        super("Shocked", true, source, totalTurns, power);
    }

    @Override
    public String onTurnStart(GameMember member) {
        member.takeEnergy((int) getPower());
        return Emote.ZAP + "**" + member.getUsername() + "** has " + formatPower() + " reduced energy this turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Shocked: Energy reduced by " + formatPower();
    }
}

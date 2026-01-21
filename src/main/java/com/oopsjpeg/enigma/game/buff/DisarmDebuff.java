package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class DisarmDebuff extends Buff {
    public DisarmDebuff(GameMember owner, GameMember source, int totalTurns) {
        super(owner, source, "Disarm", true, totalTurns, true, 0);
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        return Emote.ANGER + "**" + member.getUsername() + "** can't attack this turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Disarmed: Can't attack";
    }
}

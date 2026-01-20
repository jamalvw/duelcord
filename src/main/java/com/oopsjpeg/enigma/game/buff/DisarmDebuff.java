package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;

public class DisarmDebuff extends Buff {
    public DisarmDebuff(GameMember owner, GameMember source, int totalTurns) {
        super(owner, source, "Disarmed", true, totalTurns, 0);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Disarmed: Can't attack";
    }
}

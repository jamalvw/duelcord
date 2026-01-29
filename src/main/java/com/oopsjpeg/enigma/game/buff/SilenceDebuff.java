package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class SilenceDebuff extends Buff {
    public SilenceDebuff(GameMember owner, GameMember source, int totalTurns) {
        super(owner, source, "Silence", true, totalTurns, true, 0);
    }

    @Override
    public String onTurnStart(GameMember member) {
        return Emote.SILENCE + "**" + member.getUsername() + "** can't use skills or defend this turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Silenced: Can't use skills or defend";
    }
}

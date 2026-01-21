package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class EnGardeBuff extends Buff {
    public EnGardeBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "En Garde", false, totalTurns, true, power);
    }

    @Override
    public String onTurnStart(GameMember member) {
        return Emote.ENERGY + "**" + member.getUsername() + "** gained __" + formatPower() + "__ bonus energy this turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "En Garde: " + formatPower() + " bonus energy";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.MAX_ENERGY, getPower());
    }
}

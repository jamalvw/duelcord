package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

public class EnGardeBuff extends Buff {
    public EnGardeBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "En Garde", false, totalTurns, power);
    }

    @Override
    public String getStatus(GameMember member) {
        return "En Garde: " + formatPower() + " bonus energy on turn start";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.MAX_ENERGY, getPower());
    }

    @Override
    public String onTurnEnd(GameMember member) {
        remove();
        return "";
    }
}

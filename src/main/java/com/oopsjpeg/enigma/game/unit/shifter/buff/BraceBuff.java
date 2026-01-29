package com.oopsjpeg.enigma.game.unit.shifter.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BraceBuff extends Buff {
    public BraceBuff(GameMember owner, int totalTurns, float power) {
        super(owner, owner, "Brace", false, totalTurns, false, power);
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }

    @Override
    public String getStatus(GameMember member) {
        return "Brace: " + formatPower() + " bonus Resist";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.RESIST, getPower());
    }
}

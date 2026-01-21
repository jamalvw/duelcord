package com.oopsjpeg.enigma.game.unit.gunslinger.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class RollingBuff extends Buff {
    public RollingBuff(GameMember owner, GameMember source, float power) {
        super(owner, source, "Roll", false, 1, false, power);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Rolling: " + percent(getPower()) + " dodge chance";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(DODGE, getPower());
    }
}
package com.oopsjpeg.enigma.game.unit.shifter.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.shifter.ShifterUnit;
import com.oopsjpeg.enigma.game.unit.shifter.skill.beast.SlamSkill;
import com.oopsjpeg.enigma.game.unit.shifter.skill.normal.ZoomSkill;

import static com.oopsjpeg.enigma.util.Util.percent;

public class LeapBuff extends Buff {
    public LeapBuff(GameMember owner, int totalTurns, float power) {
        super(owner, owner, "Leap", false, totalTurns, false, power);
    }

    @Override
    public String onTurnStart(GameMember member) {
        if (!(member.getUnit() instanceof ShifterUnit)) return "";

        ShifterUnit unit = (ShifterUnit) member.getUnit();

        if (unit.getFormChanger().isDone()) {
            SlamSkill slam = unit.getBeast().getSlam();
            member.takeEnergy(25);
            return slam.act(member);
        }

        ZoomSkill zoom = unit.getNormal().getZoom();
        zoom.getCooldown().count();
        return "";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Leap: " + percent(getPower()) + " bonus Dodge Chance";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.DODGE, getPower());
    }
}

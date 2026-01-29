package com.oopsjpeg.enigma.game.unit.shifter.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.unit.shifter.ShifterUnit;

public class ZoomBuff extends Buff {
    private int attackCount = 0;

    public ZoomBuff(GameMember owner, int totalTurns) {
        super(owner, owner, "Zoom", false, totalTurns, true, 0);

        onDamageDealt(Priority.POST_DAMAGE, e -> {
            if (!e.isAttack()) return;

            attackCount++;
            if (attackCount == 2) {
                remove(true);

                if (owner.getUnit() instanceof ShifterUnit) return;

                ShifterUnit shifter = (ShifterUnit) owner.getUnit();
                shifter.stackForm();
            }
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Zoom: " + attackCount + "/" + getTotalTurns() + " Attacks";
    }
}

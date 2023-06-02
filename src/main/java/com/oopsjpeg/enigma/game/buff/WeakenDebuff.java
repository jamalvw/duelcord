package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WeakenDebuff extends Buff {
    public WeakenDebuff(GameMember source, int totalTurns, float power) {
        super("Weaken", true, source, totalTurns, power);
    }

    @Override
    public String onTurnStart(GameMember member) {
        return Emote.SILENCE + "**" + member.getUsername() + "** is weakened for **" + percent(getPower()) + "** by **" + getSource().getUsername() + "**.";
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }

    @Override
    public DamageEvent damageOut(DamageEvent event) {
        event.damage *= 1 - getPower();
        event.bonus *= 1 - getPower();
        return event;
    }
}
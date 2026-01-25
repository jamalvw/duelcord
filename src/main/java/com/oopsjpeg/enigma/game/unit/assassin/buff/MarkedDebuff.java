package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class MarkedDebuff extends Buff {
    public MarkedDebuff(GameMember owner, GameMember source, float power) {
        super(owner, source, "Mark", true, 2, false, power);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Marked: Will be Crippled by " + formatPower() + " on " + getSource().getUsername() + "'s turn";
    }

    @Override
    public String onTurnStart(GameMember member) {
        return ":bangbang: **" + member.getUsername() + "** is marked for assassination by **" + getSource().getUsername() + "**.";
    }

    @Override
    public String formatPower() {
        return percent(getPower());
    }
}
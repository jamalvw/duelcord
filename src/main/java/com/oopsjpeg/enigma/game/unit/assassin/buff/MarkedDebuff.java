package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Buff;

public class MarkedDebuff extends Buff {
    public MarkedDebuff(GameMember source) {
        super("Marked", true, source, 3, 0);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Marked: Will be Crippled on " + getSource().getUsername() + "'s turn";
    }

    @Override
    public String onTurnStart(GameMember member) {
        return ":bangbang: **" + member.getUsername() + "** is marked for assassination by **" + getSource().getUsername() + "**.";
    }
}
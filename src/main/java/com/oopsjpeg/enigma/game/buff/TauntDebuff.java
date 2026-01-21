package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.action.AttackAction;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

public class TauntDebuff extends Buff {
    public TauntDebuff(GameMember owner, GameMember source, int totalTurns) {
        super(owner, source, "Taunt", true, totalTurns, false, 0);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Taunted: Forced to attack at turn start";
    }

    @Override
    public String onTurnStart(GameMember member) {
        remove(true);
        AttackAction action = new AttackAction(member.getGame().getRandomTarget(member));
        return Emote.ANGER + "**" + member.getUsername() + "** was forced to attack!\n" + member.act(action);
    }
}

package com.oopsjpeg.enigma.game.buff;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.DamageManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.action.AttackAction;
import com.oopsjpeg.enigma.game.object.Buff;

public class TauntDebuff extends Buff {
    public TauntDebuff(GameMember owner, GameMember source, int totalTurns) {
        super(owner, source, "Taunted", true, totalTurns, 0);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Taunted: Will Attack at turn start";
    }

    @Override
    public String onTurnStart(GameMember member) {
        remove(true);
        AttackAction action = new AttackAction(member.getGame().getRandomTarget(member));
        return member.act(action);
    }
}

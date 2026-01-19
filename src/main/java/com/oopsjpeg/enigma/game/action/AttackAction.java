package com.oopsjpeg.enigma.game.action;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.DamageManager;
import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.util.Emote;

public class AttackAction implements GameAction
{
    private final GameMember target;

    public AttackAction(GameMember target)
    {
        this.target = target;
    }

    public GameMember getTarget()
    {
        return target;
    }

    @Override
    public String act(GameMember actor)
    {
        DamageEvent e = DamageManager.attack(actor, target);
        return DamageManager.process(e);
    }

    @Override
    public String getName() {
        return "Attack";
    }

    @Override
    public int getCost() {
        return 50;
    }
}
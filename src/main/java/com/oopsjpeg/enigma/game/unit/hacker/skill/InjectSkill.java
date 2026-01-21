package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.TrapType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.hacker.buff.InjectTrap;
import com.oopsjpeg.enigma.util.Emote;

public class InjectSkill extends Skill {
    public InjectSkill(Unit unit) {
        super(unit, 25, 2);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);
        TrapType trapType = actor.randomTrapType();
        InjectTrap trap = new InjectTrap(target, actor, 1, trapType);
        target.addBuff(trap, Emote.NINJA);
        return Emote.SKILL + "**" + actor.getUsername() + "** used **Inject**! Choose your next move carefully, **" + target.getUsername() + "**...";
    }

    @Override
    public String getName() {
        return "Inject";
    }

    @Override
    public String getDescription() {
        return "Place a trap that creates and activates a **Bot** matching its type."
                + "\n\n*Trap trigger could be Attacking, Defending, or Using Skills."
                + "\nOnce one type of trap is placed, that type will not reappear in the next placement.*";
    }
}

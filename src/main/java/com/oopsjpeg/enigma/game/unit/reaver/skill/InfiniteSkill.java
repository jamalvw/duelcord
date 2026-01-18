package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

public class InfiniteSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 2;

    public InfiniteSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor) {
        return new InfiniteAction(this);
    }

    @Override
    public String getName() {
        return "Infinite";
    }

    @Override
    public String getDescription() {
        return "Gain \"Infinite\" buff for 2 turns. Your damage has a 60% chance to apply Voidburn."
                + "\nVoidburn deals 5 + 10% SP damage, stacks infinitely, and lasts for 2 turns.";
    }
}

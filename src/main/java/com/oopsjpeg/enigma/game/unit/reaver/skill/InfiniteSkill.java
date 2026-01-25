package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.reaver.buff.InfiniteBuff;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.percent;

public class InfiniteSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 2;
    public static final int DAMAGE = 2;
    public static final float DAMAGE_SP_RATIO = 0.09f;

    public InfiniteSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();
        output.add(actor.addBuff(new InfiniteBuff(actor, actor, 2, 0, 0.5f), ":infinity: "));
        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Infinite";
    }

    @Override
    public String getDescription() {
        return "For the next 2 turns, your damage has a 50% chance to apply Voidburn."
                + "\nVoidburn deals __" + DAMAGE + "__ + __" + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage, stacks infinitely, and lasts for 2 turns."
                + "\nIf the enemy already has Voidburn, refresh it.";
    }

    @Override
    public String getSimpleDescription() {
        return "For the next 2 turns, your damage has a chance to apply Voidburn." +
                "\nVoidburn deals damage each turn and stacks infinitely.";
    }
}

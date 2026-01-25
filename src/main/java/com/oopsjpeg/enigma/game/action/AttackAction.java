package com.oopsjpeg.enigma.game.action;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.Units;
import com.oopsjpeg.enigma.game.unit.gunslinger.GunslingerUnit;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AttackAction implements GameAction {
    private final GameMember target;

    public AttackAction(GameMember target) {
        this.target = target;
    }

    public GameMember getTarget() {
        return target;
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();

        DamageEvent e = EventManager.attack(actor, target);
        output.add(EventManager.process(e));

        if (actor.hasGuides() && !actor.getGuides().hasAttacked()) {
            actor.getGuides().attacked();

            output.add("*Performing actions like this uses energy. Attacking used **" + getCost(actor) + "** energy, and you now have **" + actor.getEnergy() + "** left.*");

            Unit unit = actor.getUnit();
            if (unit instanceof GunslingerUnit && ((GunslingerUnit) unit).getBarrage().getCooldown().isDone() && actor.getEnergy() > 25)
                output.add("*Since you're a Gunslinger, try using **`>Barrage`**.*");
            else
                output.add("*If you're out of options, use **`>end`** to end your turn early and defend.*");
        }

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Attack";
    }

    @Override
    public int getCost(GameMember actor) {
        return 50 + actor.getStats().getInt(StatType.ATTACK_COST);
    }
}
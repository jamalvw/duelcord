package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.assassin.buff.MarkedDebuff;

import static com.oopsjpeg.enigma.util.Util.percent;

public class MarkSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;
    public static final float CRIPPLE = .3f;

    public MarkSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName() {
        return "Mark";
    }

    @Override
    public String getDescription() {
        return "Mark the enemy. If they're still marked on your next turn, consume the mark to deal __" + percent(CRIPPLE) + "__ damage to them during that turn.";
    }

    @Override
    public String getSimpleDescription() {
        return "Mark the enemy. If they're still marked on your next turn, consume the mark to deal more damage to them during that turn.";
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);
        target.addBuff(new MarkedDebuff(target, actor, CRIPPLE), ":bangbang: ");
        return ":bangbang: **" + actor.getUsername() + "** used **Mark** on **" + target.getUsername() + "**.";
    }
}

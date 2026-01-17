package com.oopsjpeg.enigma.game.unit.gunslinger.skill;

import com.oopsjpeg.enigma.game.GameAction;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;

import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public class DeadeyeSkill extends Skill {
    public static final int COOLDOWN = 3;
    public static final int COST = 50;
    public static final float JACKPOT_BARRAGE_INCREASE = 0.025f;
    public static final float CHANCE = 0.15f;
    public static final int DAMAGE = 10;
    public static final float AP_RATIO = 0.75f;
    public static final float JACKPOT_RATIO = 0.2f;

    public DeadeyeSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public GameAction act(GameMember actor) {
        return new DeadeyeAction(this, actor.getGame().getRandomTarget(actor));
    }

    @Override
    public String getName() {
        return "Deadeye";
    }

    @Override
    public String getDescription() {
        return "Deal __" + DAMAGE + "__ + __" + percent(AP_RATIO) + " Attack Power__." + "\nHas a __"
                + percent(CHANCE) + "__ chance to **Jackpot**, increased by __" + percentRaw(JACKPOT_BARRAGE_INCREASE)
                + "__ per Barrage shot hit." + "\nJackpot instead deals __" + percent(JACKPOT_RATIO)
                + "__ of the target's missing health." + "\nDeadeye can crit.";
    }
}
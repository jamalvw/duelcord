package com.oopsjpeg.enigma.game.unit.reaver.skill;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.reaver.ReaverUnit;
import com.oopsjpeg.enigma.game.unit.reaver.creature.ReaverSummon;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class SummonSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 3;
    public static final int HP = 15;
    public static final float HP_AP_RATIO = 0.7f;
    public static final float HP_SP_RATIO = 2.35f;
    public static final int DAMAGE = 5;
    public static final float DAMAGE_AP_RATIO = .4f;
    public static final float DAMAGE_SP_RATIO = .1f;

    public SummonSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String act(GameMember actor) {
        int voidPower = 2;
        if (getUnit() instanceof ReaverUnit)
            voidPower = ((ReaverUnit) getUnit()).getVoidPower();

        List<String> output = new ArrayList<>();

        for (int i = 0; i < 1 + voidPower; i++)
        {
            Stats stats = actor.getStats();
            float health = HP + (stats.get(ATTACK_POWER) * HP_AP_RATIO) + (stats.get(SKILL_POWER) * HP_SP_RATIO);
            float damage = DAMAGE + (stats.get(ATTACK_POWER) * DAMAGE_AP_RATIO) + (stats.get(SKILL_POWER) * DAMAGE_SP_RATIO);
            ReaverSummon summon = new ReaverSummon(actor, health, damage);
            output.add(actor.addSummon(summon, ":space_invader: "));
        }
        output.add(0, Emote.SKILL + "**" + actor.getUsername() + "** used **Summon**!");

        ((ReaverUnit) getUnit()).resetVoidPower();

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Summon";
    }

    @Override
    public String getDescription() {
        return "Summon a creature with __" + HP + "__ + __" + percent(HP_AP_RATIO) + " Attack Power__ + __"
                + percent(HP_SP_RATIO) + " Skill Power__ health that lasts for 3 turns. Consumes Void Power to summon up to 2 more." +
                "\nCreatures deal __" + DAMAGE + "__ + __" + percent(DAMAGE_AP_RATIO) + " Attack Power__ + __"
                + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage when I attack.\nIf I take damage, they take damage as well.";
    }
}

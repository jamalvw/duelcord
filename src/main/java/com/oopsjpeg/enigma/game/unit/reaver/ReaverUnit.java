package com.oopsjpeg.enigma.game.unit.reaver;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill;
import com.oopsjpeg.enigma.game.unit.reaver.skill.ShockSkill;
import com.oopsjpeg.enigma.game.unit.reaver.skill.SummonSkill;
import discord4j.rest.util.Color;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.game.StatType.HEALTH_PER_TURN;
import static com.oopsjpeg.enigma.util.Util.percent;

public class ReaverUnit implements Unit {
    public static final float PASSIVE_SP_RATIO = 0.15f;

    private int voidPower = 0;

    private final InfiniteSkill infinite = new InfiniteSkill(this);
    private final ShockSkill shock = new ShockSkill(this);
    private final SummonSkill summon = new SummonSkill(this);

    public int getVoidPower() {
        return voidPower;
    }

    @Override
    public DamageEvent attackOut(DamageEvent event) {
        Stats stats = event.actor.getStats();
        event.damage += PASSIVE_SP_RATIO * stats.get(SKILL_POWER);
        return event;
    }

    public String skillUsed(GameMember actor) {
        if (voidPower < 2) {
            voidPower++;

            if (voidPower >= 2) {
                return ":infinity: **" + actor.getUsername() + "** has reached **2** Void Power.";
            }
        }
        return null;
    }

    @Override
    public String getStatus(GameMember member) {
        return "Void Power: " + voidPower + " / 2";
    }

    @Override
    public String getName() {
        return "Reaver";
    }

    @Override
    public Color getColor() {
        return Color.BISMARK;
    }

    @Override
    public String getDescription() {
        return "Attacks deal __" + percent(PASSIVE_SP_RATIO) + " Skill Power__ bonus damage. Using Skills grants up to 2 stacks of Void Power.";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 125)
                .put(MAX_HEALTH, 1105)
                .put(ATTACK_POWER, 22)
                .put(HEALTH_PER_TURN, 13);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{infinite, shock, summon};
    }
}

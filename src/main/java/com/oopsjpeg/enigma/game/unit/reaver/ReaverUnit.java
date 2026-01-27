package com.oopsjpeg.enigma.game.unit.reaver;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill;
import com.oopsjpeg.enigma.game.unit.reaver.skill.ShockSkill;
import com.oopsjpeg.enigma.game.unit.reaver.skill.SummonSkill;
import discord4j.rest.util.Color;

import java.util.EnumSet;
import java.util.function.Consumer;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

public class ReaverUnit extends Unit {
    public static final float PASSIVE_SP_RATIO = 0.2f;

    private final GameMember owner;
    private final InfiniteSkill infinite = new InfiniteSkill(this);
    private final ShockSkill shock = new ShockSkill(this);
    private final SummonSkill summon = new SummonSkill(this);
    private int voidPower = 0;

    public ReaverUnit(GameMember owner) {
        this.owner = owner;

        shock.getCooldown().start(0);
        summon.getCooldown().start(0);

        hook(EventType.DAMAGE_DEALT, Priority.PRE_CALCULATION, (Consumer<DamageEvent>) event -> {
            if (event.getActor() != owner) return;
            if (!event.isAttack()) return;

            Stats stats = owner.getStats();
            event.addDamage(stats.get(SKILL_POWER) * PASSIVE_SP_RATIO);
        });
    }

    public int getVoidPower() {
        return voidPower;
    }

    public void resetVoidPower() {
        voidPower = 0;
    }

    @Override
    public String onSkillUsed(GameMember member) {
        if (voidPower < 2) {
            voidPower++;

            if (voidPower >= 2) {
                return ":infinity: **" + member.getUsername() + "** has reached max Void Power.";
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
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.BISMARK;
    }

    @Override
    public String getDescription() {
        return "Attacks deal __" + percent(PASSIVE_SP_RATIO) + " Skill Power__ bonus damage.\nUsing Skills grants up to 2 stacks of Void Power, enhancing **`>Summon`**.";
    }

    @Override
    public String getSimpleDescription() {
        return "Attacks deal bonus damage based on Skill Power.\nUsing Skills grants up to 2 stacks of Void Power, enhancing **`>Summon`**.";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 125)
                .put(MAX_HEALTH, 1390)
                .put(ATTACK_POWER, 25)
                .put(HEALTH_PER_TURN, 10);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{infinite, shock, summon};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.of(Items.FAITHBREAKER, Items.CRIMSON_MIGHT);
    }
}

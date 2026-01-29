package com.oopsjpeg.enigma.game.unit.gunslinger;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.BarrageSkill;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.DeadeyeSkill;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.RollSkill;
import discord4j.rest.util.Color;

import java.util.EnumSet;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

public class GunslingerUnit extends Unit {
    public static final float PASSIVE_SP_RATIO = .75f;

    private final GameMember owner;

    private final BarrageSkill barrage = new BarrageSkill(this);
    private final RollSkill roll = new RollSkill(this);
    private final DeadeyeSkill deadeye = new DeadeyeSkill(this);

    private boolean attackedThisRound = false;
    private int barrageShotsFired = 0;

    public GunslingerUnit(GameMember owner) {
        this.owner = owner;

        roll.getCooldown().start(0);
        deadeye.getCooldown().start(0);

        onDamageDealt(Priority.PRE_CALCULATION, event -> {
            if (!event.isAttack()) return;
            if (attackedThisRound) return;

            Stats stats = owner.getStats();
            attackedThisRound = true;
            event.setIsGoingToCrit(true);
            event.addDamage(stats.get(SKILL_POWER) * PASSIVE_SP_RATIO);
        });
    }

    public int getBarrageShotsFired() {
        return barrageShotsFired;
    }

    public void barrageShot() {
        barrageShotsFired++;
    }

    @Override
    public String getName() {
        return "Gunslinger";
    }

    @Override
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 125)
                .put(MAX_HEALTH, 1325)
                .put(ATTACK_POWER, 25)
                .put(HEALTH_PER_TURN, 17);
    }

    @Override
    public String getDescription() {
        return "Your first attack each turn always crits and deals __" + percent(PASSIVE_SP_RATIO) + " Skill Power__ bonus damage.";
    }

    @Override
    public String getSimpleDescription() {
        return "Your first attack each turn always crits and deals bonus damage.";
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{barrage, roll, deadeye};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.of(Items.IRON_SCIMITAR, Items.SOULSTEALER);
    }

    @Override
    public String onTurnStart(GameMember member) {
        attackedThisRound = false;
        return null;
    }

    public BarrageSkill getBarrage() {
        return barrage;
    }
}

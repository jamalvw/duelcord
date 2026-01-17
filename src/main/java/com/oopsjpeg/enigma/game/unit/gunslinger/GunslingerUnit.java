package com.oopsjpeg.enigma.game.unit.gunslinger;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.BarrageSkill;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.DeadeyeSkill;
import com.oopsjpeg.enigma.game.unit.gunslinger.skill.RollSkill;
import discord4j.rest.util.Color;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

public class GunslingerUnit implements Unit {
    public static final float PASSIVE_AP_RATIO = .2f;

    private final BarrageSkill barrage = new BarrageSkill(this);
    private final RollSkill roll = new RollSkill(this);
    private final DeadeyeSkill deadeye = new DeadeyeSkill(this);

    private boolean attackedThisRound = false;
    private int barrageShotsFired = 0;

    public int getBarrageShotsFired() {
        return barrageShotsFired;
    }

    public void barrageShot()
    {
        barrageShotsFired++;
    }

    @Override
    public String getName() {
        return "Gunslinger";
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 125)
                .put(MAX_HEALTH, 1090)
                .put(ATTACK_POWER, 19)
                .put(HEALTH_PER_TURN, 12);
    }

    @Override
    public String getDescription() {
        return "The first Attack per turn always Crits and deals __" + percent(PASSIVE_AP_RATIO) + " Attack Power__ bonus damage.";
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{barrage, roll, deadeye};
    }

    @Override
    public DamageEvent attackOut(DamageEvent event)
    {
        Stats stats = event.actor.getStats();

        if (!attackedThisRound)
        {
            attackedThisRound = true;
            event.crit = true;
            event.bonus += stats.get(ATTACK_POWER) * PASSIVE_AP_RATIO;
        }

        return event;
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        attackedThisRound = false;
        return null;
    }
}

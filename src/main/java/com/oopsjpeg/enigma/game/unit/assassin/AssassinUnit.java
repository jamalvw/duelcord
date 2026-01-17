package com.oopsjpeg.enigma.game.unit.assassin;

import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.Game;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.buff.CrippledDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.assassin.buff.MarkedDebuff;
import com.oopsjpeg.enigma.game.unit.assassin.buff.TracingBuff;
import com.oopsjpeg.enigma.game.unit.assassin.skill.CloakSkill;
import com.oopsjpeg.enigma.game.unit.assassin.skill.ExecuteSkill;
import com.oopsjpeg.enigma.game.unit.assassin.skill.MarkSkill;
import com.oopsjpeg.enigma.game.unit.assassin.skill.SlashSkill;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.rest.util.Color;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

public class AssassinUnit implements Unit {
    public static final int PASSIVE_DAMAGE_BASE = 10;
    public static final float PASSIVE_DAMAGE_AP_RATIO = .15f;
    public static final float PASSIVE_DAMAGE_SP_RATIO = .6f;
    public static final int PASSIVE_ENERGY_RESTORE = 25;

    private final SlashSkill slash = new SlashSkill(this);
    private final MarkSkill mark = new MarkSkill(this);
    private final ExecuteSkill execute = new ExecuteSkill(this);
    private final CloakSkill cloak = new CloakSkill(this);

    public SlashSkill getSlash() {
        return slash;
    }

    public MarkSkill getMark() {
        return mark;
    }

    public ExecuteSkill getExecute() {
        return execute;
    }

    public CloakSkill getCloak() {
        return cloak;
    }

    @Override
    public String getName() {
        return "Assassin";
    }

    @Override
    public Color getColor() {
        return Color.SEA_GREEN;
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 100)
                .put(MAX_HEALTH, 930)
                .put(ATTACK_POWER, 14)
                .put(HEALTH_PER_TURN, 15);
    }

    @Override
    public String getDescription() {
        return "After using a damaging Skill, your next Attack deals __" + PASSIVE_DAMAGE_BASE + "__ + __" + percent(PASSIVE_DAMAGE_AP_RATIO)
                + " AP__ + __" + percent(PASSIVE_DAMAGE_SP_RATIO) + " SP__ bonus damage, restores **"
                + PASSIVE_ENERGY_RESTORE + "** Energy, and resets **Slash** cooldown.";
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{slash, mark, execute, cloak};
    }

    @Override
    public String onTurnStart(GameMember member)
    {
        Game game = member.getGame();
        List<GameMember> enemies = game.getNonCurrentMembers();

        if (enemies.isEmpty()) return null;

        List<String> output = new ArrayList<>();
        for (GameMember enemy : enemies)
        {
            if (enemy.hasBuff(MarkedDebuff.class)) {
                enemy.removeBuffs(MarkedDebuff.class);
                enemy.addBuff(new CrippledDebuff(member, 1, MarkSkill.CRIPPLE), Emote.CRIPPLE);
                output.add(Emote.CRIPPLE + "**" + enemy.getUsername() + "** was marked by the assassin, suffering **Cripple** (" + percent(MarkSkill.CRIPPLE) + ").");
            }
        }

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public DamageEvent skillOut(DamageEvent event)
    {
        if (!event.cancelled && !event.actor.hasBuff(TracingBuff.class))
            event.actor.addBuff(new TracingBuff(event.actor), Emote.TRACE);
        return event;
    }
}

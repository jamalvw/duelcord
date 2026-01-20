package com.oopsjpeg.enigma.game.unit.hacker;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.hacker.bot.Bot;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotDamageEvent;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotType;
import com.oopsjpeg.enigma.game.unit.hacker.skill.BreachSkill;
import com.oopsjpeg.enigma.game.unit.hacker.skill.FirewallSkill;
import com.oopsjpeg.enigma.game.unit.hacker.skill.InjectSkill;
import com.oopsjpeg.enigma.game.unit.hacker.skill.OverloadSkill;
import com.oopsjpeg.enigma.util.Util;
import discord4j.rest.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.oopsjpeg.enigma.util.Util.percent;

public class HackerUnit implements Unit {
    public static final int BOT_LIMIT = 6;
    public static final int BOT_DAMAGE = 6;
    public static final float BOT_DAMAGE_AP_RATIO = 0.12f;
    public static final float BOT_DAMAGE_SP_RATIO = 0.25f;
    public static final float BOT_ON_HIT_RATIO = 0.4f;

    private final GameMember owner;

    private final List<Bot> bots = new ArrayList<>();

    private final BreachSkill breach = new BreachSkill(this);
    private final InjectSkill inject = new InjectSkill(this);
    private final FirewallSkill firewall = new FirewallSkill(this);
    private final OverloadSkill overload = new OverloadSkill(this);

    public HackerUnit(GameMember owner) {
        this.owner = owner;
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.SUMMONS;
                    }

                    @Override
                    public void execute(DamageEvent e) {
                        if (e.getAttacker() != getOwner()) return;
                        if (!e.isAttack()) return;

                        e.proposeEffect(() -> getBots(BotType.ATTACK).forEach(bot -> {
                            DamageEvent botStrike = new BotDamageEvent(owner, e.getVictim());
                            e.getOutput().add(DamageManager.process(botStrike));
                        }));
                    }
                }
        };
    }

    public List<Bot> getBots() {
        return bots;
    }

    public List<Bot> getBots(BotType type) {
        return getBots().stream().filter(bot -> bot.getType() == type).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "Hacker";
    }

    @Override
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

    @Override
    public String getDescription() {
        return "When the enemy activates a trap, create a **Bot**, up to **" + BOT_LIMIT + "**." +
                "\n\nBots deal __" + BOT_DAMAGE + "__ + __" + percent(BOT_DAMAGE_AP_RATIO) + " Attack Power__ + __"
                + percent(BOT_DAMAGE_SP_RATIO) + " Skill Power__ damage and apply On-Hit effects at __"
                + percent(BOT_ON_HIT_RATIO) + "__ power when activated.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Bots: " + getBots().size() + " / " + BOT_LIMIT;
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.MAX_ENERGY, 125)
                .put(StatType.ATTACK_POWER, 22)
                .put(StatType.HEALTH_PER_TURN, 19)
                .put(StatType.MAX_HEALTH, 920);
    }

    @Override
    public String onDefend(GameMember member) {
        List<String> output = new ArrayList<>();
        getBots(BotType.DEFEND).forEach(bot -> {
            DamageEvent botStrike = new BotDamageEvent(owner, member.getGame().getRandomTarget(member));
            output.add(DamageManager.process(botStrike));
        });
        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String onSkillUsed(GameMember member) {
        /*
         TODO:
          if this skill had a specific target, the bot should not choose a random target to strike
          Solution is to create a SkillUseEvent.
         */

        List<String> output = new ArrayList<>();
        getBots(BotType.SKILL).forEach(bot -> {
            DamageEvent botStrike = new BotDamageEvent(owner, member.getGame().getRandomTarget(member));
            output.add(DamageManager.process(botStrike));
        });
        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{breach, inject, firewall, overload};
    }
}

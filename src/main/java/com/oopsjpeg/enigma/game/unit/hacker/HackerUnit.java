package com.oopsjpeg.enigma.game.unit.hacker;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Items;
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
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.oopsjpeg.enigma.util.Util.percent;

public class HackerUnit extends Unit {
    public static final int BOT_LIMIT = 6;
    public static final int BOT_DAMAGE = 5;
    public static final float BOT_DAMAGE_AP_RATIO = 0.12f;
    public static final float BOT_DAMAGE_SP_RATIO = 0.35f;
    public static final float BOT_ON_HIT_RATIO = 0.25f;

    private final GameMember owner;

    private final List<Bot> bots = new ArrayList<>();

    private final BreachSkill breach = new BreachSkill(this);
    private final InjectSkill inject = new InjectSkill(this);
    private final FirewallSkill firewall = new FirewallSkill(this);
    private final OverloadSkill overload = new OverloadSkill(this);

    public HackerUnit(GameMember owner) {
        this.owner = owner;

        inject.getCooldown().start(1);
        firewall.getCooldown().start(1);
        overload.getCooldown().start(0);

        hook(EventType.DAMAGE_DEALT, Priority.SUMMONS, (Consumer<DamageEvent>) e -> {
            if (e.getActor() != getOwner()) return;
            if (!e.isAttack()) return;

            e.queueAction(() -> getBots(BotType.ATTACK).forEach(bot -> {
                DamageEvent botStrike = new BotDamageEvent(bot, owner, e.getVictim());
                e.getOutput().add(EventDispatcher.dispatch(botStrike));
            }));
        });
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
        return "Certain skills create **Bots**, up to **" + BOT_LIMIT + "**." +
                "\nBots deal __" + BOT_DAMAGE + "__ + __" + percent(BOT_DAMAGE_AP_RATIO) + " Attack Power__ + __"
                + percent(BOT_DAMAGE_SP_RATIO) + " Skill Power__ damage and apply On-Hit effects at __"
                + percent(BOT_ON_HIT_RATIO) + "__ power when activated.";
    }

    @Override
    public String getSimpleDescription() {
        return "Certain skills create **Bots**.\nBots deal damage when activated by your actions.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Bots: " + getBots().size() + " / " + BOT_LIMIT;
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(StatType.MAX_ENERGY, 125)
                .put(StatType.ATTACK_POWER, 25)
                .put(StatType.HEALTH_PER_TURN, 10)
                .put(StatType.MAX_HEALTH, 1310);
    }

    @Override
    public String onDefend(GameMember member) {
        List<String> output = new ArrayList<>();
        getBots(BotType.DEFEND).forEach(bot -> {
            DamageEvent botStrike = new BotDamageEvent(bot, owner, member.getGame().getRandomTarget(member));
            output.add(EventDispatcher.dispatch(botStrike));
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
            DamageEvent botStrike = new BotDamageEvent(bot, owner, member.getGame().getRandomTarget(member));
            output.add(EventDispatcher.dispatch(botStrike));
        });
        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{breach, inject, firewall, overload};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.of(Items.FAITHBREAKER, Items.SHADOW_REAVER);
    }
}

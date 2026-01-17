package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.buff.BleedingDebuff;
import com.oopsjpeg.enigma.game.buff.CrippledDebuff;
import com.oopsjpeg.enigma.util.Cooldown;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.Stats.*;
import static com.oopsjpeg.enigma.game.unit.UnitConstants.*;
import static com.oopsjpeg.enigma.util.Util.percent;
import static com.oopsjpeg.enigma.util.Util.percentRaw;

public enum Unit implements GameObject
{
    //BERSERKER("Berserker", Color.RED, new Stats()
    //        .put(Stats.MAX_ENERGY, 100)
    //        .put(Stats.MAX_HEALTH, 760)
    //        .put(Stats.DAMAGE, 19)
    //        .put(Stats.HEALTH_PER_TURN, 10)),
    //REAPER("Reaper", Color.of(120, 0, 0), new Stats()
    //        .put(MAX_ENERGY, 125)
    //        .put(Stats.MAX_HEALTH, 720)
    //        .put(Stats.HEALTH_PER_TURN, 7)
    //        .put(Stats.DAMAGE, 14)),
    //DUELIST("Duelist", Color.MAGENTA, new Stats()
    //        .put(Stats.MAX_ENERGY, 125)
    //        .put(Stats.MAX_HEALTH, 750)
    //        .put(Stats.DAMAGE, 21)
    //        .put(Stats.HEALTH_PER_TURN, 10)),
    //PHASEBREAKER("Phasebreaker", Color.of(0, 255, 191), new Stats()
    //        .put(Stats.MAX_ENERGY, 125)
    //        .put(Stats.MAX_HEALTH, 750)
    //        .put(Stats.DAMAGE, 18)
    //        .put(Stats.HEALTH_PER_TURN, 10)),
    //THIEF("Thief", Color.YELLOW, new Stats()
    //        .put(Stats.MAX_ENERGY, 150)
    //        .put(Stats.MAX_HEALTH, 735)
    //        .put(Stats.DAMAGE, 17)
    //        .put(Stats.HEALTH_PER_TURN, 8)
    //        .put(Stats.CRIT_CHANCE, 0.2f)
    //        .put(Stats.CRIT_DAMAGE, -1 * .2f)),
    //WARRIOR("Warrior", Color.CYAN, new Stats()
    //        .put(MAX_ENERGY, 125)
    //        .put(MAX_HEALTH, 775)
    //        .put(ATTACK_POWER, 22)
    //        .put(HEALTH_PER_TURN, 12)) {
    //    private static final String VAR_PASSIVE_COUNT = "passive_count";
    //    private static final String VAR_BASH_COOLDOWN = "bash_cooldown";
//
    //    public Stacker getPassiveCount(GameMemberVars vars) {
    //        if (!vars.has(this, VAR_PASSIVE_COUNT))
    //            setPassiveCount(vars, new Stacker(WARRIOR_PASSIVE_LIMIT));
    //        return vars.get(this, VAR_PASSIVE_COUNT, Stacker.class);
    //    }
//
    //    public void setPassiveCount(GameMemberVars vars, Stacker passiveCount) {
    //        vars.put(this, VAR_PASSIVE_COUNT, passiveCount);
    //    }
//
    //    public Cooldown getBashCooldown(GameMemberVars vars) {
    //        if (!vars.has(this, VAR_BASH_COOLDOWN))
    //            setBashCooldown(vars, new Cooldown(WARRIOR_BASH_COOLDOWN));
    //        return vars.get(this, VAR_BASH_COOLDOWN, Cooldown.class);
    //    }
//
    //    public void setBashCooldown(GameMemberVars vars, Cooldown bashCooldown) {
    //        vars.put(this, VAR_BASH_COOLDOWN, bashCooldown);
    //    }
//
    //    @Override
    //    public String getDescription() {
    //        return "Every **" + WARRIOR_PASSIVE_LIMIT + "** Attacks, deal __" + percent(WARRIOR_PASSIVE_AP_RATIO) + " Attack Power__ bonus damage.";
    //    }
//
    //    @Override
    //    public String[] getTopic(GameMember member) {
    //        GameMemberVars vars = member.getVars();
    //        Stacker passiveCount = getPassiveCount(vars);
    //        Cooldown bashCooldown = getBashCooldown(vars);
//
    //        return new String[]{
    //                "Bonus: **" + passiveCount.getCurrent() + " / 3**",
    //                bashCooldown.isDone()
    //                        ? "Bash: **Ready**"
    //                        : "Bash: **" + bashCooldown.getCurrent() + "** turns"};
    //    }
//
    //    @Override
    //    public Skill[] getSkills() {
    //        return new Skill[] { new BashSkill() };
    //    }
//
    //    @Override
    //    public String onTurnStart(GameMember member) {
    //        GameMemberVars vars = member.getVars();
    //        Cooldown bashCooldown = getBashCooldown(vars);
    //        boolean bashReady = bashCooldown.count();
    //        boolean bashNotify = bashReady && bashCooldown.tryNotify();
//
    //        setBashCooldown(vars, bashCooldown);
//
    //        if (bashReady && bashNotify)
    //            return Emote.INFO + "**" + member.getUsername() + "**'s Bash is ready to use.";
//
    //        return null;
    //    }
//
    //    @Override
    //    public DamageEvent attackOut(DamageEvent event) {
    //        GameMemberVars vars = event.actor.getVars();
    //        Stacker passiveCount = getPassiveCount(vars);
//
    //        if (passiveCount.stack()) {
    //            Stats stats = event.actor.getStats();
    //            event.bonus += stats.get(ATTACK_POWER) * WARRIOR_PASSIVE_AP_RATIO;
    //            passiveCount.reset();
    //        }
//
    //        setPassiveCount(vars, passiveCount);
//
    //        return event;
    //    }
//
    //    class BashCommand implements Command {
    //        @Override
    //        public void execute(Message message, String[] args) {
    //            User author = message.getAuthor().orElse(null);
    //            MessageChannel channel = message.getChannel().block();
    //            Game game = Enigma.getInstance().getPlayer(author).getGame();
    //            GameMember member = game.getMember(author);
//
    //            if (channel.equals(game.getChannel()) && member.equals(game.getCurrentMember())) {
    //                message.delete().subscribe();
    //                if (member.hasBuff(SilenceDebuff.class))
    //                    Util.sendFailure(channel, "You cannot **Bash** while silenced.");
    //                else {
    //                    GameMemberVars vars = member.getVars();
    //                    Cooldown bashCooldown = getBashCooldown(vars);
//
    //                    if (!bashCooldown.isDone())
    //                        Util.sendFailure(channel, "**Bash** is on cooldown for **" + bashCooldown.getCurrent() + "** more turns.");
    //                    else
    //                        member.act(new BashAction(game.getRandomTarget(member)));
    //                }
    //            }
    //        }
//
    //        @Override
    //        public String getName() {
    //            return "bash";
    //        }
//
    //        @Override
    //        public String getDescription() {
    //            return "Break the target's shield and resist, then deal __" + percent(WARRIOR_BASH_AP_RATIO) + " Attack Power__ + __" + percent(WARRIOR_BASH_SP_RATIO) + " Skill Power__." +
    //                    "\nBash stacks Warrior's passive, but doesn't activate it.";
    //        }
    //    }
//
    //    class BashAction implements GameAction {
    //        private final GameMember target;
//
    //        public BashAction(GameMember target) {
    //            this.target = target;
    //        }
//
    //        @Override
    //        public String act(GameMember actor) {
    //            GameMemberVars vars = actor.getVars();
    //            Cooldown bashCooldown = getBashCooldown(vars);
    //            Stacker passiveCount = getPassiveCount(vars);
//
    //            bashCooldown.start();
    //            passiveCount.stack();
//
    //            setBashCooldown(vars, bashCooldown);
    //            setPassiveCount(vars, passiveCount);
//
    //            DamageEvent event = new DamageEvent(actor, target);
    //            Stats actorStats = event.actor.getStats();
    //            Stats targetStats = event.target.getStats();
//
    //            event.target.setDefensive(false);
//
    //            if (targetStats.get(Stats.RESIST) > 0) {
    //                targetStats.put(Stats.RESIST, 0);
    //                event.output.add(Emote.SHIELD + " It broke their resist!");
    //            }
//
    //            if (event.target.hasShield()) {
    //                event.target.setShield(0);
    //                event.output.add(Emote.SHIELD + " It broke their shield!");
    //            }
//
    //            event.damage += actorStats.get(ATTACK_POWER) * WARRIOR_BASH_AP_RATIO;
    //            event.damage += actorStats.get(SKILL_POWER) * WARRIOR_BASH_SP_RATIO;
//
    //            event = event.actor.skill(event);
//
    //            return actor.damage(event, Emote.KNIFE, "Bash");
    //        }
//
    //        @Override
    //        public int getEnergy() {
    //            return 25;
    //        }
    //    }
    //};
}

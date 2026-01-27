package com.oopsjpeg.enigma.game.unit.shifter;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.skill.SlamSkill;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;
import discord4j.rest.util.Color;

import java.util.EnumSet;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

// GNAR
public class ShifterUnit extends Unit {
    public static final int PASSIVE_DAMAGE = 10;
    public static final float PASSIVE_DAMAGE_SP_RATIO = 0.15f;
    public static final float PASSIVE_WEAKEN_CHANCE = 0.4f;

    private final GameMember owner;

    private final SlamSkill slam = new SlamSkill(this);

    private final Stacker form = new Stacker(2);

    public ShifterUnit(GameMember owner) {
        this.owner = owner;

        //hook(DamageEvent.class, new Hook<DamageEvent>() {
        //    @Override
        //    public DamagePhase getPhase() {
        //        return DamagePhase.POST_DAMAGE;
        //    }
//
        //    @Override
        //    public void execute(DamageEvent e) {
        //        if (e.getActor() != getOwner()) return;
        //        if (!e.isAttack() && !e.isSkill()) return;
        //        if (!form.isDone()) return;
//
        //        if (RANDOM.nextFloat() < PASSIVE_WEAKEN_CHANCE)
        //            e.proposeEffect(() -> {
        //                if (!e.getVictim().hasBuff(WeakenedDebuff.class))
        //                    e.getOutput().add(e.getVictim().addBuff(new WeakenedDebuff(e.getVictim(), e.getActor(), 1, 0.45f), Emote.WEAKEN));
        //            });
        //    }
        //});
    }

    @Override
    public String getName() {
        return "Shifter";
    }

    @Override
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.DARK_GOLDENROD;
    }

    @Override
    public String getDescription() {
        return "Every 3 turns, enter **Beast** form, increasing max health and damage but losing **25** energy."
                + "\n\n**Normal Form**: Every other attack or skill deals __" + PASSIVE_DAMAGE + "__ + __" + percent(PASSIVE_DAMAGE_SP_RATIO) + " Skill Power__ bonus damage."
                + "\n\n**Beast Form**: Attacks and skills have a __" + percent(PASSIVE_WEAKEN_CHANCE) + "__ chance to Weaken by 30%.";
    }

    @Override
    public String getSimpleDescription() {
        return "";
    }

    @Override
    public Stats getStats() {
        if (form.isDone())
            return new Stats()
                    .put(MAX_ENERGY, 125)
                    .put(MAX_HEALTH, 1450)
                    .put(ATTACK_POWER, 31)
                    .put(SKILL_POWER, 20)
                    .put(HEALTH_PER_TURN, 9);
        else
            return new Stats()
                    .put(MAX_ENERGY, 150)
                    .put(MAX_HEALTH, 1250)
                    .put(ATTACK_POWER, 11)
                    .put(HEALTH_PER_TURN, 18);
    }

    @Override
    public String onTurnStart(GameMember member) {
        if (form.isDone()) {
            return Emote.DRAGON + "**" + member.getUsername() + "** entered **Beast Form**!";
        }
        return "";
    }

    @Override
    public String onTurnEnd(GameMember member) {
        if (form.isDone()) {
            form.reset();
            return Emote.DRAGON + "**" + member.getUsername() + "** entered **Normal Form**.";
        } else if (form.stack())
            return Emote.DRAGON + "**" + member.getUsername() + "** is about to transform...!";
        return "";
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{slam};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.noneOf(Items.class);
    }
}

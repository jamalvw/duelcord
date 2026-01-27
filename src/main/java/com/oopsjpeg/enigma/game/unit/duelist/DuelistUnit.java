package com.oopsjpeg.enigma.game.unit.duelist;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.buff.BleedDebuff;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.duelist.skill.BlitzSkill;
import com.oopsjpeg.enigma.game.unit.duelist.skill.DuelSkill;
import com.oopsjpeg.enigma.game.unit.duelist.skill.ParrySkill;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;
import discord4j.rest.util.Color;

import java.util.EnumSet;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.percent;

public class DuelistUnit extends Unit {
    public static final int PASSIVE_DAMAGE = 5;
    public static final float PASSIVE_DAMAGE_HP_RATIO = 0.06f;

    private final GameMember owner;

    private final Stacker bleed = new Stacker(4);

    private final DuelSkill duel = new DuelSkill(this);
    private final BlitzSkill blitz = new BlitzSkill(this);
    private final ParrySkill parry = new ParrySkill(this);

    public DuelistUnit(GameMember owner) {
        this.owner = owner;

        duel.getCooldown().start(0);
        blitz.getCooldown().start(0);
        parry.getCooldown().start(0);

        onDamageDealt(Priority.PRE_CALCULATION, e -> {
            if (!e.isOnHit()) return;

            e.queueAction(() -> {
                if (bleed.stack()) {
                    BleedDebuff buff = new BleedDebuff(e.getVictim(), owner, 2, (e.getVictim().getHealth() * PASSIVE_DAMAGE_HP_RATIO) / 2);
                    e.getOutput().add(e.getVictim().addBuff(buff, Emote.BLEED));
                    bleed.reset();
                }
            });
        });
    }

    @Override
    public String getName() {
        return "Duelist";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Bleed: " + bleed.getCurrent() + " / " + bleed.getMax();
    }

    @Override
    public GameMember getOwner() {
        return owner;
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA;
    }

    @Override
    public String getDescription() {
        return "Every **4** Hits, apply a Bleed for __" + PASSIVE_DAMAGE + "__ + __" + percent(PASSIVE_DAMAGE_HP_RATIO) + " enemy current health__ damage over 2 turns.";
    }

    @Override
    public String getSimpleDescription() {
        return "Every **4** Hits, apply a Bleed for 2 turns.";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(MAX_ENERGY, 125)
                .put(MAX_HEALTH, 1420)
                .put(ATTACK_POWER, 22)
                .put(HEALTH_PER_TURN, 7);
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{duel, blitz, parry};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.of(Items.SOULSTEALER, Items.SHADOW_REAVER);
    }
}

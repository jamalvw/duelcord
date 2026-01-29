package com.oopsjpeg.enigma.game.unit.shifter;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.UnitForm;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.object.Items;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.form.BeastForm;
import com.oopsjpeg.enigma.game.unit.shifter.form.NormalForm;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;
import discord4j.rest.util.Color;

import java.util.EnumSet;

import static com.oopsjpeg.enigma.game.StatType.*;
import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

// GNAR
public class ShifterUnit extends Unit {
    public static final int NORMAL_DAMAGE = 8;
    public static final float NORMAL_DAMAGE_SP_RATIO = 0.2f;
    public static final float BEAST_WEAKEN_CHANCE = 0.35f;
    public static final float BEAST_WEAKEN_AMOUNT = 0.2f;

    private final GameMember owner;

    private final NormalForm normal = new NormalForm(this);
    private final BeastForm beast = new BeastForm(this);

    private final Stacker formChanger = new Stacker(3);
    private final Stacker normalPassiveStacker = new Stacker(2);

    public ShifterUnit(GameMember owner) {
        this.owner = owner;

        onDamageDealt(Priority.POST_DAMAGE, e -> {
            if (!e.isAttack() && !e.isSkill()) return;

            if (formChanger.isDone()) {
                if (RANDOM.nextFloat() < BEAST_WEAKEN_CHANCE)
                    e.queueAction(() -> {
                        e.getOutput().add(e.getVictim().addBuff(new WeakenedDebuff(e.getVictim(), e.getActor(), 1, BEAST_WEAKEN_AMOUNT), Emote.WEAKEN));
                    });
            } else {
                e.multiplyDamage(0.7f);

                if (!normalPassiveStacker.stack()) return;

                e.addDamage(NORMAL_DAMAGE);
                e.addDamage(e.getActor().getStats().get(SKILL_POWER) * NORMAL_DAMAGE_SP_RATIO);
                normalPassiveStacker.reset();
            }
        });
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
    public String getStatus(GameMember member) {
        return "Form: " + (formChanger.isDone() ? "Beast" : "Normal (" + formChanger.getCurrent() + "/" + formChanger.getMax() + ")");
    }

    @Override
    public String getDescription() {
        return "Every 3 turns, enter **Beast** form, increasing stats but losing __25__ energy."
                + "\n\n**Normal Form**: Attacks deal 70% damage, but every other Attack or Skill deals __" + NORMAL_DAMAGE + "__ + __" + percent(NORMAL_DAMAGE_SP_RATIO) + " Skill Power__ bonus damage."
                + "\n\n**Beast Form**: Attacks and Skills have a __" + percent(BEAST_WEAKEN_CHANCE) + "__ chance to __Weaken__ by __" + percent(BEAST_WEAKEN_AMOUNT) + "__.";
    }

    @Override
    public String getSimpleDescription() {
        return "Every 3 turns, enter **Beast** form, increasing stats but losing __25__ energy."
                + "\n\n**Normal Form**: Attacks deal less damage, but every other Attack or Skill deals bonus damage."
                + "\n**Beast Form**: Attacks and Skills have a chance to __Weaken__.";
    }

    @Override
    public Stats getStats() {
        if (formChanger.isDone())
            return new Stats()
                    .put(MAX_ENERGY, 125)
                    .put(MAX_HEALTH, 1450)
                    .put(ATTACK_POWER, 35)
                    .put(SKILL_POWER, 20)
                    .put(HEALTH_PER_TURN, 9);
        else
            return new Stats()
                    .put(MAX_ENERGY, 150)
                    .put(MAX_HEALTH, 1205)
                    .put(ATTACK_POWER, 15)
                    .put(HEALTH_PER_TURN, 12);
    }

    @Override
    public String onTurnStart(GameMember member) {
        return stackForm();
    }

    @Override
    public String onTurnEnd(GameMember member) {
        if (formChanger.isDone()) {
            formChanger.reset();
            return Emote.DRAGON + "**" + member.getUsername() + "** is back in **Normal Form**.";
        } else if (formChanger.getCurrent() == formChanger.getMax() - 1)
            return Emote.DRAGON + "**" + member.getUsername() + "** is about to transform...!";
        return "";
    }

    @Override
    public UnitForm getForm() {
        return formChanger.isDone() ? beast : normal;
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[0];
    }

    @Override
    public UnitForm[] getForms() {
        return new UnitForm[]{normal, beast};
    }

    @Override
    public EnumSet<Items> getRecommendedBuild() {
        return EnumSet.noneOf(Items.class);
    }

    public Stacker getFormChanger() {
        return formChanger;
    }

    public String stackForm() {
        if (formChanger.stack())
            return Emote.DRAGON + "**" + owner.getUsername() + "** entered **Beast Form**!";
        return "";
    }

    public BeastForm getBeast() {
        return beast;
    }

    public NormalForm getNormal() {
        return normal;
    }
}

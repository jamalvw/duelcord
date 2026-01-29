package com.oopsjpeg.enigma.game.unit.shifter.skill.beast;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.buff.LeapBuff;
import com.oopsjpeg.enigma.game.unit.shifter.form.BeastForm;
import com.oopsjpeg.enigma.game.unit.shifter.skill.normal.LeapSkill;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import static com.oopsjpeg.enigma.util.Util.percent;

public class SlamSkill extends Skill {
    public static final int DAMAGE = 22;
    public static final float DAMAGE_AP_RATIO = 0.8f;
    public static final float DAMAGE_SP_RATIO = 0.2f;

    public static final float HEAL = 0.25f;

    public SlamSkill(Unit unit) {
        super(unit, 25, 2);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();
        GameMember target = actor.getGame().getRandomTarget(actor);

        DamageEvent e = new DamageEvent(actor, target);
        e.setSource("Slam");
        e.setIsSkill(true);
        e.addDamage(DAMAGE);
        e.addDamage(stats.get(StatType.ATTACK_POWER) * DAMAGE_AP_RATIO);
        e.addDamage(stats.get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);

        if (actor.hasBuff(LeapBuff.class)) {
            Buff buff = actor.getBuff(LeapBuff.class);
            actor.removeBuff(buff);
            e.multiplyDamage(1 + LeapSkill.SLAM_INCREASE);
        }

        e.queueAction(() -> {
            if (target.hasBuff(WeakenedDebuff.class)) {
                e.addHealing(target.getHealth() * HEAL);
            }
        });

        return EventDispatcher.dispatch(e);
    }

    @Override
    public Mono<?> execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel().block();

        if (!(getUnit().getForm() instanceof BeastForm))
            return Util.sendFailure(channel, "**" + getName() + "** can only be used in **Beast** form.");

        return super.execute(message, args);
    }

    @Override
    public String getName() {
        return "Slam";
    }

    @Override
    public String getDescription() {
        return "Deal __" + DAMAGE + "__ + __" + percent(DAMAGE_AP_RATIO) + "__ + __" + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage."
                + "\nIf the enemy is Weakened, heal for __" + percent(HEAL) + "__ of the damage dealt.";
    }

    @Override
    public String getSimpleDescription() {
        return "Deal damage. If the enemy is Weakened, heal for some of the damage dealt.";
    }
}

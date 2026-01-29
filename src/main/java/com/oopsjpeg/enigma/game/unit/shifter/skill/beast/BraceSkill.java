package com.oopsjpeg.enigma.game.unit.shifter.skill.beast;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.HealEvent;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.buff.BraceBuff;
import com.oopsjpeg.enigma.game.unit.shifter.form.BeastForm;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import static com.oopsjpeg.enigma.util.Util.percent;

public class BraceSkill extends Skill {
    public static final int HEAL = 45;
    public static final float HEAL_SP_RATIO = 0.25f;
    public static final float RESIST = 0.2f;

    public BraceSkill(Unit unit) {
        super(unit, 25, 5);
    }

    @Override
    public String act(GameMember actor) {
        Stats stats = actor.getStats();

        actor.setEnergy(0);
        actor.addBuff(new BraceBuff(actor, 1, RESIST), Emote.SKILL);

        HealEvent heal = new HealEvent(actor, HEAL);
        heal.setSource("Brace");
        heal.addAmount(stats.get(StatType.SKILL_POWER) * HEAL_SP_RATIO);

        return Emote.SKILL + "**" + actor.getUsername() + "** used **Brace**, and gained __" + percent(RESIST) + "__ Resist.\n" + EventDispatcher.dispatch(heal);
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
        return "Brace";
    }

    @Override
    public String getDescription() {
        return "End the turn early, healing __" + HEAL + "__ + __" + percent(HEAL_SP_RATIO) + " Skill Power__ and gaining __" + percent(RESIST) + "__ bonus Resist.";
    }

    @Override
    public String getSimpleDescription() {
        return "End the turn early, healing and gain bonus Resist.";
    }
}

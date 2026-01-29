package com.oopsjpeg.enigma.game.unit.shifter.skill.beast;

import com.oopsjpeg.enigma.game.GameMember;
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
    public static final float RESIST = 0.45f;

    public BraceSkill(Unit unit) {
        super(unit, 25, 5);
    }

    @Override
    public String act(GameMember actor) {
        actor.setEnergy(0);
        actor.addBuff(new BraceBuff(actor, 1, RESIST), Emote.SKILL);
        return Emote.SKILL + "**" + actor.getUsername() + "** used **Brace**, gaining __" + percent(RESIST) + "__ Resist.";
    }

    @Override
    public Mono<?> execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel().block();

        if (!(getUnit().getForm() instanceof BeastForm))
            return Util.sendFailure(channel, "**" + getName() + "** can only be used in **Beast Form**.");

        return super.execute(message, args);
    }

    @Override
    public String getName() {
        return "Brace";
    }

    @Override
    public String getDescription() {
        return "End the turn early and gain __" + percent(RESIST) + "__ bonus Resist.";
    }

    @Override
    public String getSimpleDescription() {
        return "End the turn early and gain bonus Resist.";
    }
}

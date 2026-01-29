package com.oopsjpeg.enigma.game.unit.shifter.skill.normal;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.buff.ZoomBuff;
import com.oopsjpeg.enigma.game.unit.shifter.form.NormalForm;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class ZoomSkill extends Skill {
    private static final int DAMAGE = 15;
    private static final float DAMAGE_SP_RATIO = 0.5f;

    public ZoomSkill(Unit unit) {
        super(unit, 25, 2);
    }

    @Override
    public String act(GameMember actor) {
        ZoomBuff buff = new ZoomBuff(actor, 1);
        actor.addBuff(buff, Emote.SKILL);
        return Emote.SKILL + "**" + actor.getUsername() + "** used **Zoom**!";
    }

    @Override
    public Mono<?> execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel().block();

        if (!(getUnit().getForm() instanceof NormalForm))
            return Util.sendFailure(channel, "**" + getName() + "** can only be used in **Normal Form**.");

        return super.execute(message, args);
    }

    @Override
    public String getName() {
        return "Zoom";
    }

    @Override
    public String getDescription() {
        return "Your next 2 attacks this turn deal __" + DAMAGE + "__ + __" + DAMAGE_SP_RATIO + " Skill Power__. The second attack reduces Beast Form's cooldown.";
    }

    @Override
    public String getSimpleDescription() {
        return "Your next 2 Attacks this turn deal bonus damage."
                + "\nThe second Attack reduces **Beast Form**'s cooldown.";
    }
}

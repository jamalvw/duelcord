package com.oopsjpeg.enigma.game.unit.shifter.skill.normal;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.ShifterUnit;
import com.oopsjpeg.enigma.game.unit.shifter.buff.LeapBuff;
import com.oopsjpeg.enigma.game.unit.shifter.form.NormalForm;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.percent;

public class LeapSkill extends Skill {
    public final static float DODGE_CHANCE = 0.3f;
    public final static float SLAM_INCREASE = 0.35f;

    public LeapSkill(Unit unit) {
        super(unit, 25, 3);
    }

    @Override
    public String act(GameMember actor) {
        List<String> output = new ArrayList<>();
        LeapBuff buff = new LeapBuff(actor, 1, DODGE_CHANCE);
        actor.addBuff(buff, Emote.SKILL);
        actor.setEnergy(0);

        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Leap** and gained __" + percent(DODGE_CHANCE) + " Dodge Chance__ until next turn!");

        if (actor.getUnit() instanceof ShifterUnit) {
            ShifterUnit shifter = (ShifterUnit) actor.getUnit();
            if (shifter.getFormChanger().getCurrent() == shifter.getFormChanger().getMax() - 1) {
                output.add(Emote.DRAGON + "This might hurt...");
            }
        }

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public Mono<?> execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel().block();

        if (!(getUnit().getForm() instanceof NormalForm))
            return Util.sendFailure(channel, "**" + getName() + "** can only be used in **Normal** form.");

        return super.execute(message, args);
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public String getDescription() {
        return "End the turn early and gain __" + percent(DODGE_CHANCE) + " Dodge Chance__."
                + "\nIf you enter **Beast Form** on your next turn, immediately **Slam** with __" + percent(SLAM_INCREASE) + "__ bonus damage."
                + "\nOtherwise, **Zoom**'s cooldown is reduced by 1.";
    }

    @Override
    public String getSimpleDescription() {
        return "End the turn early and gain bonus __Dodge Chance__."
                + "\n\nIf you enter **Beast Form** on your next turn, immediately **Slam** with bonus damage."
                + "\nOtherwise, **Zoom**'s cooldown is reduced.";
    }
}

package com.oopsjpeg.enigma.game.unit.shifter.skill.beast;

import com.oopsjpeg.enigma.game.EventDispatcher;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.shifter.form.BeastForm;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.percent;

public class RoarSkill extends Skill {
    public static final int DAMAGE = 35;
    public static final float DAMAGE_SP_RATIO = 1.2f;

    public RoarSkill(Unit unit) {
        super(unit, 25, 4);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);

        List<String> output = new ArrayList<>();
        List<String> extendedDebuffs = new ArrayList<>();

        target.getBuffs().stream()
                .filter(Buff::isDebuff)
                .forEach(debuff -> {
                    debuff.extend(1);
                    extendedDebuffs.add(debuff.getName());
                });

        if (extendedDebuffs.size() >= 2) {
            Stats stats = actor.getStats();
            DamageEvent e = new DamageEvent(actor, target);
            e.setSource("Roar");
            e.setIsSkill(true);
            e.setEmote(Emote.DRAGON);
            e.addDamage(DAMAGE);
            e.addDamage(stats.get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);
            output.add(EventDispatcher.dispatch(e));
        }

        return Util.joinNonEmpty("\n", output);
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
        return "Roar";
    }

    @Override
    public String getDescription() {
        return "Extend all enemy debuffs by __1__ turn."
                + "\nIf the enemy has 2+ debuffs, deal __" + percent(DAMAGE) + "__ + __"
                + percent(DAMAGE_SP_RATIO) + " Skill Power__ damage.";
    }

    @Override
    public String getSimpleDescription() {
        return "Extend all enemy debuffs by __1__ turn."
                + "\nIf the enemy has 2+ debuffs, deal damage.";
    }
}

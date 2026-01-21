package com.oopsjpeg.enigma.game.unit.hacker.skill;

import com.oopsjpeg.enigma.game.EventManager;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.buff.DisarmDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.game.unit.hacker.HackerUnit;
import com.oopsjpeg.enigma.game.unit.hacker.bot.Bot;
import com.oopsjpeg.enigma.game.unit.hacker.bot.BotDamageEvent;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

public class OverloadSkill extends Skill {
    private final static float DISARM_CHANCE = 0.3f;
    private final static float BONUS_DAMAGE = 0.3f;

    public OverloadSkill(Unit unit) {
        super(unit, 50, 4);
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);
        HackerUnit unit = (HackerUnit) actor.getUnit();
        List<String> output = new ArrayList<>();

        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Overload**!" + (unit.getBots().isEmpty() ? " ...But there were no bots to activate." : ""));

        for (Bot bot : unit.getBots()) {
            BotDamageEvent e = new BotDamageEvent(bot, actor, target);
            float rand = RANDOM.nextFloat();
            if (rand < DISARM_CHANCE) {
                if (!target.hasBuff(DisarmDebuff.class)) {
                    e.proposeEffect(() -> {
                        DisarmDebuff debuff = new DisarmDebuff(target, actor, 1);
                        e.getOutput().add(target.addBuff(debuff, Emote.ANGER));
                    });
                } else {
                    e.multiplyDamage(1 + BONUS_DAMAGE);
                }
            }
            output.add(EventManager.process(e));
        }

        unit.getBots().clear();

        return Util.joinNonEmpty("\n", output);
    }

    @Override
    public String getName() {
        return "Overload";
    }

    @Override
    public String getDescription() {
        return "Activate and destroy every **Bot**."
                + "\nEach Bot has a __" + percent(DISARM_CHANCE)+ "__ chance to Disarm."
                + "\nIf they’re already Disarmed, deal __" + percent(BONUS_DAMAGE) + "__ bonus damage.";
    }
}

package com.oopsjpeg.enigma.game.unit.assassin.skill;

import com.oopsjpeg.enigma.game.*;
import com.oopsjpeg.enigma.game.buff.BleedingDebuff;
import com.oopsjpeg.enigma.game.object.Skill;
import com.oopsjpeg.enigma.game.unit.Unit;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static com.oopsjpeg.enigma.game.StatType.SKILL_POWER;
import static com.oopsjpeg.enigma.util.Util.percent;

public class SlashSkill extends Skill {
    public static final int COST = 25;
    public static final int COOLDOWN = 2;
    public static final int DAMAGE_BASE = 15;
    public static final float DAMAGE_AP_RATIO = .35f;
    public static final float DAMAGE_SP_RATIO = .2f;
    public static final float BLEED_CHANCE = .25f;
    public static final int BLEED_TURNS = 2;
    public static final float BLEED_DAMAGE_RATIO = .3f;

    public SlashSkill(Unit unit) {
        super(unit, COST, COOLDOWN);
    }

    @Override
    public String getName()
    {
        return "Slash";
    }

    @Override
    public String getDescription()
    {
        return "Deal __" + DAMAGE_BASE + "__ + __" + percent(DAMAGE_AP_RATIO) + " AP__ + __"
                + percent(DAMAGE_SP_RATIO) + " SP__.\n" + "Has a __" + percent(BLEED_CHANCE)
                + "__ chance to Bleed for __" + percent(BLEED_DAMAGE_RATIO) + "__ of damage dealt over **"
                + BLEED_TURNS + "** turns.";
    }

    @Override
    public String act(GameMember actor) {
        GameMember target = actor.getGame().getRandomTarget(actor);

        Stats stats = actor.getStats();
        List<String> output = new ArrayList<>();
        output.add(Emote.SKILL + "**" + actor.getUsername() + "** used **Slash**!");

        DamageEvent e = new DamageEvent(actor, target);
        e.setIsSkill(true);
        e.setEmote(Emote.KNIFE);
        e.setSource("Slash");
        e.addDamage(DAMAGE_BASE);
        e.addDamage(stats.get(ATTACK_POWER) * DAMAGE_AP_RATIO);
        e.addDamage(stats.get(SKILL_POWER) * DAMAGE_SP_RATIO);
        e.proposeEffect(() -> {
            float rand = Util.RANDOM.nextFloat();
            if (rand <= BLEED_CHANCE)
            {
                float bleedDamage = e.getDamage() * BLEED_DAMAGE_RATIO;
                output.add(target.addBuff(new BleedingDebuff(target, actor, BLEED_TURNS, bleedDamage), Emote.BLEED));
            }
        });

        output.add(DamageManager.process(e));

        return Util.joinNonEmpty("\n", output);
    }
}

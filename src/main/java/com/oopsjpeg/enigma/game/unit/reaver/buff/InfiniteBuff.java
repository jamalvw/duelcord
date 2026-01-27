package com.oopsjpeg.enigma.game.unit.reaver.buff;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.StatType;
import com.oopsjpeg.enigma.game.object.Buff;
import com.oopsjpeg.enigma.util.Emote;

import static com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill.DAMAGE;
import static com.oopsjpeg.enigma.game.unit.reaver.skill.InfiniteSkill.DAMAGE_SP_RATIO;
import static com.oopsjpeg.enigma.util.Util.RANDOM;
import static com.oopsjpeg.enigma.util.Util.percent;

public class InfiniteBuff extends Buff {
    private final float chance;

    public InfiniteBuff(GameMember owner, GameMember source, int totalTurns, float power, float chance) {
        super(owner, source, "Infinite", false, totalTurns, true, power);
        this.chance = chance;

        onDamageDealt(Priority.POST_DAMAGE, e -> {
            if (e.isDoT()) return;

            float damage = DAMAGE + (getOwner().getStats().get(StatType.SKILL_POWER) * DAMAGE_SP_RATIO);

            if (RANDOM.nextFloat() < chance) {
                e.queueAction(() -> {
                    GameMember victim = e.getVictim();
                    if (!victim.hasBuff(VoidburnDebuff.class)) {
                        VoidburnDebuff burn = new VoidburnDebuff(victim, e.getActor(), 2, damage);
                        e.getOutput().add(victim.addBuff(burn, Emote.VOIDBURN));
                    } else {
                        VoidburnDebuff burn = (VoidburnDebuff) victim.getBuff(VoidburnDebuff.class);
                        burn.setPower(burn.getPower() + damage);
                        burn.reset();
                        e.getOutput().add(Emote.VOIDBURN + "**Voidburn**'s damage increased to **" + burn.formatPower() + "**!");
                    }
                });
            }
        });
    }

    @Override
    public String getStatus(GameMember member) {
        return "Infinite: " + percent(chance) + " chance to apply Voidburn";
    }
}

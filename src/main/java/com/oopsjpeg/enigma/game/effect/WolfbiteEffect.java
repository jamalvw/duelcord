package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WolfbiteEffect extends Effect {
    private final Stacker attackCount;

    public WolfbiteEffect(GameMember owner, int attackLimit, float power) {
        super(owner, "Wolfbite", power, null);
        this.attackCount = new Stacker(attackLimit);

        onDamageDealt(Priority.PRE_CALCULATION, e -> {
            if (!e.isAttack()) return;

            e.queueAction(() -> {
                if (attackCount.stack()) {
                    e.getOutput().add(e.getVictim().addBuff(new WeakenedDebuff(e.getActor(), e.getVictim(), 1, getPower()), Emote.WEAKEN));
                    attackCount.reset();
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return "Every __" + attackCount.getMax() + "__ Attacks, __Weaken__ the target by __" + percent(getPower()) + "__ on their next turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Wolfbite: " + attackCount.getCurrent() + "/" + attackCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.buff.WeakenedDebuff;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class WolfbiteEffect extends Effect {
    private final Stacker attackCount;

    public WolfbiteEffect(GameMember owner, int attackLimit, float power) {
        super(owner, "Wolfbite", power, null);
        this.attackCount = new Stacker(attackLimit);

        hook(EventType.DAMAGE_DEALT, Priority.PRE_CALCULATION, (Consumer<DamageEvent>) e -> {
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
        return "Every **" + attackCount.getMax() + "** Attacks, **Weaken** the target by __" + percent(getPower()) + "__ on their next turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Wolfbite: " + attackCount.getCurrent() + "/" + attackCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

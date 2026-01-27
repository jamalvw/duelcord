package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.buff.WoundedDebuff;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import static com.oopsjpeg.enigma.util.Util.percent;

public class LifewasterEffect extends Effect {
    private final Stacker hitCount;

    public LifewasterEffect(GameMember owner, int hitLimit, float power) {
        super(owner, "Lifewaster", power, null);
        hitCount = new Stacker(hitLimit);

        onDamageDealt(Priority.PRE_CALCULATION, event -> {
            if (!event.isOnHit()) return;

            event.queueAction(() -> {
                if (hitCount.stack()) {
                    event.getOutput().add(event.getVictim().addBuff(new WoundedDebuff(event.getVictim(), event.getActor(), 1, getPower()), Emote.WOUND));
                    hitCount.reset();
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return "Every __" + hitCount.getMax() + "__ hits, __Wound__ the target by __" + percent(getPower()) + "__ on their next turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Lifewaster: " + hitCount.getCurrent() + "/" + hitCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

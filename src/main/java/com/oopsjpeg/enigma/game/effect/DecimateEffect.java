package com.oopsjpeg.enigma.game.effect;

import com.oopsjpeg.enigma.game.EventType;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Priority;
import com.oopsjpeg.enigma.game.buff.CrippleDebuff;
import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.game.object.Effect;
import com.oopsjpeg.enigma.util.Emote;
import com.oopsjpeg.enigma.util.Stacker;

import java.util.function.Consumer;

import static com.oopsjpeg.enigma.util.Util.percent;

public class DecimateEffect extends Effect {
    private final Stacker critCount;

    public DecimateEffect(GameMember owner, int critLimit, float power) {
        super(owner, "Decimate", power, null);
        this.critCount = new Stacker(critLimit);

        hook(EventType.DAMAGE_DEALT, Priority.POST_DAMAGE, (Consumer<DamageEvent>) event -> {
            if (event.getActor() != getOwner()) return;
            if (!event.isGoingToCrit()) return;

            event.queueAction(() -> {
                if (critCount.stack()) {
                    event.getOutput().add(event.getVictim().addBuff(new CrippleDebuff(event.getVictim(), event.getActor(), 1, getPower()), Emote.CRIPPLE));
                    critCount.reset();
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return "Every **" + critCount.getMax() + "** Crits, **Cripple** the target by __" + percent(getPower()) + "__ until their next turn.";
    }

    @Override
    public String getStatus(GameMember member) {
        return "Decimate: " + critCount.getCurrent() + "/" + critCount.getMax() + " (" + percent(getPower()) + ")";
    }
}

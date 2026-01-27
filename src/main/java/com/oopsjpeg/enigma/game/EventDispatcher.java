package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.game.event.DamageEvent;
import com.oopsjpeg.enigma.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static java.lang.Math.round;

public class EventDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);

    public static DamageEvent attack(GameMember attacker, GameMember victim) {
        DamageEvent event = new DamageEvent(attacker, victim);
        event.setIsAttack(true);
        event.setIsOnHit(true);
        event.setIsAbleToCrit(true);
        event.addDamage(attacker.getStats().get(ATTACK_POWER));
        event.queueAction(() -> {
            Game game = attacker.getGame();
            int rawGoldGain = round(Util.nextInt(20, 30) + (game.getTurnCount() * 0.5f));
            int finalGoldGain = game.getMode().handleGold(rawGoldGain);
            attacker.giveGold(finalGoldGain);
        });

        return event;
    }

    public static <T extends Event> String dispatch(T event) {
        GameMember actor = event.getActor();

        if (!actor.hasHealth()) return null;

        // Create a pipeline
        var pipeline = event.createPipeline();
        // Sort the pipeline by priority
        pipeline.sort(Comparator.comparing(Hook::getPriority));
        // Execute the hooks
        pipeline.forEach(hook -> {
            LOGGER.debug("Dispatching event to hook {}", hook.getClass().getName());
            ((Hook<T>) hook).execute(event);
        });

        // Finalize the event
        if (!event.isCancelled()) {
            LOGGER.debug("Finalizing event");
            event.finish();
        }

        return Util.joinNonEmpty("\n", event.getOutput());
    }
}

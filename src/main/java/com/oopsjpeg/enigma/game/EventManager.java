package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static java.lang.Math.round;

public class EventManager {
    public static DamageEvent attack(GameMember attacker, GameMember victim) {
        DamageEvent event = new DamageEvent(attacker, victim);
        event.setIsAttack(true);
        event.setIsOnHit(true);
        event.setIsAbleToCrit(true);
        event.addDamage(attacker.getStats().get(ATTACK_POWER));
        event.proposeEffect(() -> {
            Game game = attacker.getGame();
            int rawGoldGain = round(Util.nextInt(20, 30) + (game.getTurnCount() * 0.5f));
            int finalGoldGain = game.getMode().handleGold(rawGoldGain);
            attacker.giveGold(finalGoldGain);
        });

        return event;
    }

    public static <T extends Event> String process(T event)
    {
        Game game = event.getGame();
        GameMember actor = event.getActor();

        if (!actor.hasHealth()) return null;

        // Create pipeline
        List<Hook<?>> pipeline = event.createPipeline();

        // Sort by priority
        pipeline.sort(Comparator.comparing(Hook::getPhase));

        // Execute the pipeline
        for (Hook<? extends Event> hook : pipeline) {
            System.out.println("Dispatching event to " + hook.getClass().getSimpleName());
            event.dispatchTo(hook);
        }

        // Final damage application
        if (!event.isCancelled()) {
            System.out.println("Completing event");
            event.complete();
            //event.getOutput().add(attacker.updateStats());
            //event.getOutput().add(victim.updateStats());

            //if (!event.getVictim().hasHealth())
            //    event.getOutput().add(event.getVictim().lose());
        }

        return Util.joinNonEmpty("\n", event.getOutput());
    }
}

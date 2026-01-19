package com.oopsjpeg.enigma.game;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_POWER;
import static java.lang.Math.ceil;
import static java.lang.Math.round;

public class DamageManager {
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

    public static String process(DamageEvent event)
    {
        Game game = event.getGame();
        GameMember attacker = event.getAttacker();
        GameMember victim = event.getVictim();

        List<DamageHook> pipeline = new ArrayList<>();

        // Add dynamic hooks
        pipeline.addAll(attacker.getDamageHooks());
        pipeline.addAll(victim.getDamageHooks());

        // Add system hooks
        pipeline.add(new CriticalStrikeHook());
        pipeline.add(new ResistanceHook());
        pipeline.add(new ShieldHook());
        pipeline.add(new LifeStealHook());
        pipeline.add(new DodgeHook());

        // Sort by priority
        pipeline.sort(Comparator.comparing(DamageHook::getPhase));

        // Execute the pipeline
        for (DamageHook hook : pipeline) {
            if (event.isCancelled()) break;

            hook.execute(event);
        }

        // Final damage application
        if (!event.isCancelled()) {
            event.getOutput().add(attacker.updateStats());
            event.getOutput().add(victim.updateStats());

            for (PendingAction action : event.getEffects())
                action.execute();

            game.getMode().handleDamage(event);

            if (event.getHealing() > 0)
                event.getOutput().add(attacker.heal((int) ceil(event.getHealing())));
            if (event.getShielding() > 0)
                event.getOutput().add(attacker.shield((int) ceil(event.getShielding())));

            // Summon damaging
            //if (event.target.hasBlockingSummon())
            //{
            //    Summon summon = event.target.getBlockingSummon();
            //    summon.takeHealth(event.damage + event.bonus);
            //    if (summon.getHealth() <= 0) summon.remove();
            //    event.output.add(0, Util.damageText(event, event.actor.getUsername(), event.target.getUsername() + "'s " + summon.getName(), emote, source));
            //}

            if (event.getDamage() > 0) {
                event.getVictim().takeHealth(round(event.getDamage()));
                event.getOutput().add(0, Util.damageText(event, attacker.getUsername(), event.getVictim().getUsername(), event.getEmote(), event.getSource()));
                if (!event.getVictim().hasHealth())
                    event.getOutput().add(event.getVictim().lose());
            }
        }

        return Util.joinNonEmpty("\n", event.getOutput());
    }
}

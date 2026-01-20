package com.oopsjpeg.enigma.game.unit.duelist.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.game.StatType.ATTACK_COST;

public class BlitzBuff extends Buff {
    public BlitzBuff(GameMember owner, GameMember source, int totalTurns, float power) {
        super(owner, source, "Blitz", false, totalTurns, power);
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.POST_DAMAGE;
                    }

                    @Override
                    public void execute(DamageEvent event) {
                        event.multiplyDamage(getPower());
                    }
                }
        };
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(ATTACK_COST, -25);
    }

    @Override
    public String getStatus(GameMember member) {
        return "Blitz: Attacks cost 25";
    }

    @Override
    public String onTurnEnd(GameMember member) {
        remove(true);
        return "";
    }
}

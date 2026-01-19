package com.oopsjpeg.enigma.game.unit.assassin.buff;

import com.oopsjpeg.enigma.DamageHook;
import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Stats;
import com.oopsjpeg.enigma.game.object.Buff;

import static com.oopsjpeg.enigma.game.StatType.DODGE;
import static com.oopsjpeg.enigma.util.Util.percent;

public class CloakedBuff extends Buff {
    public CloakedBuff(GameMember owner, GameMember source, float power) {
        super(owner, source, "Cloaked", false, 2, power);
    }

    @Override
    public DamageHook[] getDamageHooks() {
        return new DamageHook[]{
                new DamageHook() {
                    @Override
                    public DamagePhase getPhase() {
                        return DamagePhase.VALIDATION;
                    }

                    @Override
                    public void execute(DamageEvent e) {
                        if (e.getVictim() != getOwner()) return;
                        if (!e.isSkill()) return;

                        e.proposeEffect(() -> remove());
                    }
                }
        };
    }

    @Override
    public String getStatus(GameMember member) {
        return "Cloaked: " + percent(getPower()) + " bonus Dodge until damaged by Skill";
    }

    @Override
    public Stats getStats() {
        return new Stats()
                .put(DODGE, getPower());
    }
}
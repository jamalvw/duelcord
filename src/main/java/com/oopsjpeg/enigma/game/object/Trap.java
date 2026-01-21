package com.oopsjpeg.enigma.game.object;

import com.oopsjpeg.enigma.DamagePhase;
import com.oopsjpeg.enigma.TrapType;
import com.oopsjpeg.enigma.game.DamageEvent;
import com.oopsjpeg.enigma.game.GameMember;
import com.oopsjpeg.enigma.game.Hook;

public abstract class Trap extends Buff {
    private final TrapType type;

    public Trap(GameMember owner, GameMember source, String name, int totalTurns, float power, TrapType type) {
        super(owner, source, name, true, totalTurns, true, power);
        this.type = type;

        hook(DamageEvent.class, new Hook<DamageEvent>() {
            @Override
            public DamagePhase getPhase() {
                return DamagePhase.POST_DAMAGE;
            }

            @Override
            public void execute(DamageEvent e) {
                if (type != TrapType.ATTACK) return;
                if (e.getActor() != getOwner()) return;
                if (!e.isAttack()) return;

                e.getOutput().add(onActivated());
                remove(true);
            }
        });
    }

    @Override
    public String onDefend(GameMember member) {
        if (type != TrapType.DEFEND) return null;

        remove(true);
        return onActivated();
    }

    @Override
    public String onSkillUsed(GameMember member) {
        if (type != TrapType.SKILL) return null;

        remove(true);
        return onActivated();
    }

    public TrapType getType() {
        return type;
    }

    public abstract String onActivated();
}
